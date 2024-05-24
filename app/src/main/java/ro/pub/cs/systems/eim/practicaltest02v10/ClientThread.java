package ro.pub.cs.systems.eim.practicaltest02v10;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread {

    private final String address;
    private final int port;
    private final String pokemonName;
    private final TextView pokemonResultTextView;

    private Socket socket;

    public ClientThread(String address, int port, String pokemon, TextView pokemonResultTextView) {
        this.address = address;
        this.port = port;
        this.pokemonName = pokemon;
        this.pokemonResultTextView = pokemonResultTextView;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(address, port);
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            printWriter.println(pokemonName);
            printWriter.flush();
            String pokemonInformation;
            while ((pokemonInformation = bufferedReader.readLine()) != null) {
                final String finalizedPokemonInformation = pokemonInformation;
                pokemonResultTextView.post(() -> pokemonResultTextView.setText(finalizedPokemonInformation));
            }
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                }
            }
        }
    }

}