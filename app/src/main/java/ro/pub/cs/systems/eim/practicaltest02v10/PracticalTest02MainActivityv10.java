package ro.pub.cs.systems.eim.practicaltest02v10;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ro.pub.cs.systems.eim.practicaltest02.R;

public class PracticalTest02MainActivityv10 extends AppCompatActivity {

    private EditText serverPortEditText = null;
    private EditText clientAddressEditText = null;
    private EditText clientPortEditText = null;
    private EditText pokemonNameEditText = null;
    private TextView pokemonResultTextView = null;

    private ServerThread serverThread = null;

    private final ConnectButtonClickListener connectButtonClickListener = new ConnectButtonClickListener();
    private class ConnectButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            serverThread = new ServerThread(Integer.parseInt("5555"));
            if (serverThread.getServerSocket() == null) {
                Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not create server thread!");
                return;
            }
            serverThread.start();
        }

    }

    private final GetWeatherForecastButtonClickListener getWeatherForecastButtonClickListener = new GetWeatherForecastButtonClickListener();
    private class GetWeatherForecastButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            String pokemon = pokemonNameEditText.getText().toString();
            if (pokemon.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Parameters from client (pokemon name) should be filled", Toast.LENGTH_SHORT).show();
                return;
            }

            pokemonResultTextView.setText(Constants.EMPTY_STRING);

            ClientThread clientThread = new ClientThread(
                    "10.0.2.15", Integer.parseInt("5555"), pokemon, pokemonResultTextView
            );
            clientThread.start();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onCreate() callback method has been invoked");
        setContentView(R.layout.activity_practical_test02v10_main);

        Button connectButton = (Button) findViewById(R.id.connect_button);
        connectButton.setOnClickListener(connectButtonClickListener);

        pokemonNameEditText = (EditText)findViewById(R.id.pokemon_name_edit_text);
        Button getPokemonInfoButton = (Button) findViewById(R.id.get_pokemon_info);
        getPokemonInfoButton.setOnClickListener(getWeatherForecastButtonClickListener);
        pokemonResultTextView = (TextView)findViewById(R.id.pokemon_result_text_view);
    }

    @Override
    protected void onDestroy() {
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onDestroy() callback method has been invoked");
        if (serverThread != null) {
            serverThread.stopThread();
        }
        super.onDestroy();
    }

}