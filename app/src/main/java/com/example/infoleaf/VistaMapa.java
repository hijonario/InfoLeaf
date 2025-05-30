package com.example.infoleaf;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class VistaMapa extends Dialog {

    private WebView webView;
    private Context context;

    public VistaMapa(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_map);

        webView = findViewById(R.id.webview_map);
        Button btnClose = findViewById(R.id.btn_close);

        setupWebView();
        loadSigpac();

        btnClose.setOnClickListener(v -> dismiss());
    }

    private void setupWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.addJavascriptInterface(new WebAppInterface(this), "Android");
    }

    private void loadSigpac() {
        String html = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Mapa España</title>\n" +
                "    <link rel=\"stylesheet\" href=\"https://unpkg.com/leaflet@1.7.1/dist/leaflet.css\" />\n" +
                "    <script src=\"https://unpkg.com/proj4@2.7.5/dist/proj4.js\"></script>\n" +
                "    <style>\n" +
                "        body { margin: 0; padding: 0; }\n" +
                "        #map { height: 100vh; width: 100%; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div id=\"map\"></div>\n" +
                "    <script src=\"https://unpkg.com/leaflet@1.7.1/dist/leaflet.js\"></script>\n" +
                "    <script>\n" +
                "        // Definir la proyección UTM huso 30N (EPSG:25830)\n" +
                "        proj4.defs('EPSG:25830', '+proj=utm +zone=30 +ellps=GRS80 +units=m +no_defs');\n" +
                "        \n" +
                "        var map = L.map('map').setView([40.4165, -3.70256], 6);\n" +
                "        \n" +
                "        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {\n" +
                "            attribution: '&copy; OpenStreetMap contributors'\n" +
                "        }).addTo(map);\n" +
                "        \n" +
                "        map.on('click', function(e) {\n" +
                "            // Convertir WGS84 (lat/lng) a UTM 30N (x/y)\n" +
                "            var utmCoords = proj4('EPSG:4326', 'EPSG:25830', [e.latlng.lng, e.latlng.lat]);\n" +
                "            var x = Math.round(utmCoords[0]);\n" +
                "            var y = Math.round(utmCoords[1]);\n" +
                "            \n" +
                "            // Mostrar marcador con coordenadas UTM\n" +
                "            if (window.marker) map.removeLayer(window.marker);\n" +
                "            window.marker = L.marker(e.latlng).addTo(map)\n" +
                "                .bindPopup(`X (UTM): ${x}<br>Y (UTM): ${y}`)\n" +
                "                .openPopup();\n" +
                "            \n" +
                "            // Enviar a Android\n" +
                "            Android.handleMapClick(x, y);\n" +
                "        });\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";

        webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
    }

    public static class WebAppInterface {
        VistaMapa vistaMapa;

        WebAppInterface(VistaMapa vistaMapa) {
            this.vistaMapa = vistaMapa;
        }

        @JavascriptInterface
        public void handleMapClick(double x, double y) {

            new Thread(() -> {
                HttpURLConnection conn = null;
                BufferedReader reader = null;

                try {
                    String apiUrl = "http://10.0.2.2:5000/parcela_catastro?x=" + x + "&y=" + y;

                    URL url = new URL(apiUrl);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);

                    int responseCode = conn.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String line;

                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }

                        String jsonResponse = response.toString();

                        if (jsonResponse.trim().isEmpty()) {
                            throw new Exception("La respuesta de la API está vacía");
                        }

                        ((android.app.Activity) vistaMapa.context).runOnUiThread(() -> {
                            vistaMapa.procesarRespuestaAPI(jsonResponse);
                        });
                    } else {
                        throw new Exception("Error HTTP: " + responseCode);
                    }

                } catch (Exception e) {
                    final String errorMsg = "Error al consultar la API: " + e.getMessage();
                    e.printStackTrace();

                    ((android.app.Activity) vistaMapa.context).runOnUiThread(() -> {
//                        Toast.makeText(vistaMapa.context, errorMsg, Toast.LENGTH_LONG).show();
                        Log.e("API_ERROR", errorMsg, e);
                    });
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            }).start();
        }
    }

    private void procesarRespuestaAPI(String jsonResponse) {
        try {
            JSONObject json = new JSONObject(jsonResponse);

            String municipio = json.optString("municipio", "");
            String provincia = json.optString("provincia", "");
            String parcela = json.optString("parcela", "");
            String poligono = json.optString("poligono", "");
            String superficie = json.optString("superficie_ha", "");

            String ubicacion = municipio + ", " + provincia;

            JSONObject datosFormateados = new JSONObject();
            datosFormateados.put("ubicacion", ubicacion);
            datosFormateados.put("parcela", parcela);
            datosFormateados.put("poligono", poligono);
            datosFormateados.put("superficie", superficie);

            if (listener != null) {
                listener.onDataReceived(datosFormateados.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
//            Toast.makeText(context, "Error al procesar los datos", Toast.LENGTH_SHORT).show();
        }
    }

    public interface OnParcelaDataReceived {
        void onDataReceived(String datosParcela);
    }

    private OnParcelaDataReceived listener;

    public void setOnParcelaDataReceivedListener(OnParcelaDataReceived listener) {
        this.listener = listener;
    }



}
