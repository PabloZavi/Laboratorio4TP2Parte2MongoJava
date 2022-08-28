package main;

import com.mongodb.MongoClient;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

public class Main {

    public static void main(String[] args) {
        
        Controlador contr = new Controlador();
        
        //PARTE 2 EJERCICIO 5
        //5.1 Buscar por region
        //contr.bucarPaisesPorRegion("Americas");
        //5.2 Buscar por región y población mayor a determinado número
        //contr.paisesPorRegionYMayorPoblacion("Americas", 100000000);
        //5.3 Buscar paises descartaando una region
        //contr.bucarPaisesQueNoSonDeUnaRegion("Africa");
        //5.4 Cambiar el nombre y la poblacion a un pais (paisABuscar/nuevoNombrePais/nuevaPoblacion)
        //contr.actualizarNombrePaisYPoblacion("Egypt", "Egipto", 95000000);
        //5.5 Eliminar pais por código
        //contr.eliminarPais(258);
        //5.6
        //Al ejecutar el método drop() sobre una base de datos se elimina la base de datos y sus documentos asociados
        //Al ejecutar el método drop() sobre un documento (coleccion) se elimina el documento y sus índices asociados
        //5.7 Seleccionar los países con poblacion entre dos numeros
        //contr.paisesConPoblacionEntreDosNumeros(50000000, 150000000);
        //5.8 Muestro todos los países ordenados por nombre
        //contr.mostrarPaisesOrdenadosPorNombre();
        //5.9 El método skip() saltea los primeros N registros de la colección
        //contr.mostrarPaisesSaltandoLosPrimerosNPaises(2);
        //5.10 Respecto a lo que sería el operador LIKE de SQL en Mongo DB:
        //Recordemos que el operador LIKE en SQL busca porciones de cadena
        //Por ej: SELECT * FROM paises WHERE name LIKE "%arg%";
        //En MongoDB sería con expresiones regulares así: collection.find({"name":/.*arg.*});
        //5.11 Crear un nuevo índice usando el code de cada país
        //contr.usarCreateIndex();
        //5.12 BackUp de la base de datos Mongo
        //Se usa la siguiente instrucción desde la consola del sistema (no desde Mongo Shell):
        //mongodump --db dbName --out outFile --host "IP:PORT"  --username <user>  --password <pass>
        //mongodump  --db paises_db --out "C:\mongodb\dump" --host "127.0.0.1:27017"
        //Para restaurarla:
        //mongorestore --db newDB "pathOfOldBackup"
        //mongorestore  --db paises_db_nueva  "C:\mongodb\dump\paises_db" --host "127.0.0.1:27017"
        
        
        for (int i = 1; i <= 300; i++) {
        
        try {
                //creamos una URL donde esta nuestro webservice
                URL url = new URL("https://restcountries.com/v2/callingcode/" + i);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                //indicamos por que verbo HTML ejecutaremos la solicitud
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                //si la respuesta del servidor es distinta al codigo 200 lanzaremos una Exception
                //(El código 200 es que la página funciona)
                System.out.println(i + " El país no existe");
                //throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }
                if (conn.getResponseCode() == 200) { //Si el servidor responde con 200 la página existe
                    BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

                    //creamos un StringBuilder para almacenar la respuesta del web service
                    StringBuilder sb = new StringBuilder();
                    int cp;
                    while ((cp = br.read()) != -1) {
                        sb.append((char) cp);
                    }
                    //en la cadena output almacenamos toda la respuesta del servidor
                    String jsonString = sb.toString();

                    //El Json devuelto es un array aunque tenga sólo un objeto, entonces convertimos el
                    //Json a un array de Json
                    JSONArray myJson = new JSONArray(jsonString);
                    //Ahora sacamos el objeto Json del array (está en la posición 0) y lo 
                    //convertimos a un JSONObject
                    JSONObject object = myJson.getJSONObject(0);


                    //Convertimos al objeto Json a un String
                    String JsonToStringPais = object.toString();

                    if(contr.existePais(i)){
                        contr.actualizarPais(i, JsonToStringPais);
                        System.out.println(i + " pais actualizado");
                    }
                    else {
                        contr.insertarPais(JsonToStringPais);
                        System.out.println(i + " pais agregado");
                    }
                    

        
                    
                    conn.disconnect();
                    //contr.mongoClient.close();
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        
        }
//        
//        System.out.println("Las bases de datos son: " + contr.traerDatos());
//        
//        contr.mongoClient.close();
        
    }
    
}
