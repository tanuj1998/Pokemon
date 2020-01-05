package com.tanuj.pokemon;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        CheckUserPermsions();
        LoadPokemon();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    void CheckUserPermsions(){
        if ( Build.VERSION.SDK_INT >= 23){
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED  ){
                requestPermissions(new String[]{
                                android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return ;
            }
        }

        LocationListner();// init the contact list

    }
    //get acces to location permsion
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LocationListner();// init the contact list
                } else {
                    // Permission Denied
                    Toast.makeText( this,"your message" , Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    void  LocationListner(){
        LocationListener locationListener = new MyLocationListener(this);
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3, 3, locationListener);


        myThread m_thread= new myThread();
        m_thread.start();
    }

    Location oldLocation;
    class myThread extends Thread{

        myThread(){
            oldLocation = new Location("Start");
            oldLocation.setLatitude(0);
            oldLocation.setLongitude(0);

        }
        public void run(){
            while (true){
                try{
                    Thread.sleep(1000);

                    if(oldLocation.distanceTo(MyLocationListener.location) == 0){
                        continue;
                    }
                    else{
                        oldLocation = MyLocationListener.location;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mMap.clear();

                            // Add a marker in Sydney and move the camera
                            LatLng sydney = new LatLng(MyLocationListener.location.getLatitude(), MyLocationListener.location.getLongitude());
                            mMap.addMarker(new MarkerOptions().position(sydney).title("Me")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.mario))
                            );
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,14));

                            for(int i = 0; i < ListPokemons.size(); i++) {
                                Pokemon pokemon = ListPokemons.get(i);

                                //Check if pokemon not caught
                                if (pokemon.isCatch == false) {
                                    LatLng pokemonLocation =
                                            new LatLng(pokemon.location.getLatitude(), pokemon.location.getLongitude());
                                    mMap.addMarker(new MarkerOptions().position(pokemonLocation).
                                            title(pokemon.name)
                                            .snippet(pokemon.des + ",power:" + pokemon.power)
                                            .icon(BitmapDescriptorFactory.fromResource(pokemon.Image))
                                    );

                                    //Catch a pokemon
                                    if(MyLocationListener.location.distanceTo(pokemon.location) < 2){
                                        MyPower += pokemon.power;
                                        Toast.makeText(MapsActivity.this,"Caught pokemon, new power is:" + MyPower, Toast.LENGTH_LONG).show();
                                        pokemon.isCatch = true;
                                        ListPokemons.set(i,pokemon);
                                    }
                                }
                            }

                        }
                    });



                }catch (Exception ex){}
            }
        }
    }

    double MyPower = 0;

    ArrayList<Pokemon> ListPokemons = new ArrayList<>();

    void LoadPokemon(){
        ListPokemons.add(new Pokemon(R.drawable.bulbasaur, "Bulbasur","lives in africa",55,37.478489,-122.239435));
        ListPokemons.add(new Pokemon(R.drawable.charmander, "Charmender","lives in Japan",70,37.463684,-122.244024));
        ListPokemons.add(new Pokemon(R.drawable.squirtle, "Squirtle","lives in Usa",84,37.471723,-122.233128));

    }
}
