package main;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientException;
import java.util.List;

public class BaseDeDatos {
    
    //Para crear una conexion con Mongo, primero tenemos que hacer clic derecho en "Dependencies"-->
    //Add Dependency y en "Search-->Query" ponemos "MongoDB", seleccionamos "MongoDB Driver",
    //desplegamos las opciones y ponemos la última versión.
    
    MongoClient mongo = null;
    
    public MongoClient crearConexion(){
        
        String servidor = "localhost";
        int puerto = 27017;
        
        try {
            mongo = new MongoClient(servidor, puerto);
        } catch (MongoClientException e) {
            System.out.println("Error en la conexión a MongoDB: " + e.toString());
        }
        
        return mongo;
    }
    
    
    public void cierraConexion() {
        try {
            mongo.close();
        } catch (MongoClientException e) {
            e.printStackTrace();
        }
    }
    
    
    
}
