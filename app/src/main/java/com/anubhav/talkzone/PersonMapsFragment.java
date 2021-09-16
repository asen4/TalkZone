package com.anubhav.talkzone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;

public class PersonMapsFragment extends Fragment {

    private FirebaseAuth mAuth;
    private String currentUserID, locationName;
    private DatabaseReference mUsersReference;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(final GoogleMap googleMap) {
            mAuth = FirebaseAuth.getInstance();
            mUsersReference = FirebaseDatabase.getInstance().getReference().child("Users");
            currentUserID = mAuth.getCurrentUser().getUid();

            mUsersReference.child(currentUserID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    locationName = dataSnapshot.child("countryName").getValue().toString();

                    try {
                        if (getActivity() != null) {
                            List<Address> addressList = new Geocoder(getActivity()).getFromLocationName(locationName, 1);
                            if (addressList != null) {
                                Address location = addressList.get(0);
                                LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
                                googleMap.addMarker(new MarkerOptions().position(position).title(locationName));
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 3));
                            }
                        }
                    }

                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_person_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
}