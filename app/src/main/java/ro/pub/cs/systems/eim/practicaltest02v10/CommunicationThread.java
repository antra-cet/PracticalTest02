package ro.pub.cs.systems.eim.practicaltest02v10;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;

public class CommunicationThread extends Thread {

    private final ServerThread serverThread;
    private final Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);

            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client pokemonName!");
            String pokemonName = bufferedReader.readLine();
            if (pokemonName == null || pokemonName.isEmpty()) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client pokemonName!");
                return;
            }

            HashMap<String, PokemonInformation> data = serverThread.getData();
            PokemonInformation pokemonInformation;
            if (data.containsKey(pokemonName)) {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the cache...");
                pokemonInformation = data.get(pokemonName);
            } else {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the webservice...");
                HttpClient httpClient = new DefaultHttpClient();
                String pageSourceCode = "";
                HttpGet httpGet = new HttpGet(Constants.WEB_SERVICE_ADDRESS + pokemonName);
                HttpResponse httpGetResponse = httpClient.execute(httpGet);
                HttpEntity httpGetEntity = httpGetResponse.getEntity();
                if (httpGetEntity != null) {
                    pageSourceCode = EntityUtils.toString(httpGetEntity);
                }

                JSONObject content = new JSONObject(pageSourceCode);

                JSONArray abilitiesArray = content.getJSONArray(Constants.ABILITIES);
                JSONObject abilities;
                StringBuilder conditionAbilities = new StringBuilder();
                conditionAbilities.append("Abilities: ");
                for (int i = 0; i < abilitiesArray.length(); i++) {
                    abilities = abilitiesArray.getJSONObject(i);
                    JSONObject abilityName = abilities.getJSONObject(Constants.ABILITY);
                    conditionAbilities.append(abilityName.getString(Constants.ABILITY_NAME));

                    if (i < abilitiesArray.length() - 1) {
                        conditionAbilities.append(";");
                    } else {
                        conditionAbilities.append(", ");
                    }
                }

                JSONArray typeArray = content.getJSONArray(Constants.TYPES);
                JSONObject type;
                StringBuilder conditionTypes = new StringBuilder();
                conditionTypes.append("Type: ");
                for (int i = 0; i < typeArray.length(); i++) {
                    type = typeArray.getJSONObject(i);
                    JSONObject typeName = type.getJSONObject(Constants.TYPE);
                    conditionTypes.append(typeName.getString(Constants.TYPE_NAME));

                    if (i < typeArray.length() - 1) {
                        conditionTypes.append(";");
                    } else {
                        conditionTypes.append(", ");
                    }
                }

                pokemonInformation = new PokemonInformation(
                        conditionAbilities.toString(), conditionTypes.toString()
                );
                serverThread.setData(pokemonName, pokemonInformation);
            }
            if (pokemonInformation == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Weather Forecast Information is null!");
                return;
            }
            StringBuilder result = new StringBuilder();
            result.append(pokemonInformation.getTypes() + ";" + pokemonInformation.getAbility());
            printWriter.println(result.toString());
            printWriter.flush();
        } catch (IOException | JSONException ioException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException ioException) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            }
        }
    }

}