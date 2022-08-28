package main;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Filters.lt;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.util.JSON;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import org.bson.conversions.Bson;

public class Controlador {
    
    BaseDeDatos conexion = new BaseDeDatos();
    MongoClient mongoClient = conexion.crearConexion();
    
    /*
    public List<String> traerBasesDeDatosMongo(){
       List<String> basesDeDatos = mongoClient.getDatabaseNames();
       return basesDeDatos;
    }*/
    
    public void insertarPais(String pais){
        //Traigo la base de datos correspondiente
        MongoDatabase db = mongoClient.getDatabase("laboratorio4Practico2");
        //Traigo la colección correspondiente de esa base de datos, que es en donde insertaré el país
        MongoCollection<Document> collection = db.getCollection("pais");
        //Parseo de Json a formato Document el parámetro pais
        Document document = Document.parse(pais);
        //Lo inserto
        collection.insertOne(document);
    }
    
    public void actualizarPais(int codePais, String pais){
        //Traigo la base de datos correspondiente
        MongoDatabase db = mongoClient.getDatabase("laboratorio4Practico2");
        //Traigo la colección correspondiente de esa base de datos
        MongoCollection<Document> collection = db.getCollection("pais");
        
        //Busco el país a actualizar mediante su código (que está en un array llamado "callingCodes"
        String codeAbuscar = "{\"callingCodes\":[\"" + codePais + "\"]}";
        //y creo un documento parseando ese string a formato Document
        Document findDocument = new Document(Document.parse(codeAbuscar));
        
        //Y mediante este método reemplazo todo el contenido del documento por el String
        //pais pasado como parámetro (tengo que parsear a Document los strings)
        collection.findOneAndReplace(findDocument, Document.parse(pais));
        
    }
    
    public boolean existePais(int codePais){
        boolean existe = false;
        
        //Traigo la base de datos correspondiente
        MongoDatabase db = mongoClient.getDatabase("laboratorio4Practico2");
        //Traigo la colección correspondiente de esa base de datos en la cual buscaré
        MongoCollection<Document> collection = db.getCollection("pais");
        
        //Creo un documento con los criterios de búsqueda
        //En este caso hago un String Json de lo que quiero buscar en la base de datos
        //(el código del país que está en un array llamado "callingCodes")
        String callingCodes = "{\"callingCodes\":[\"" + codePais + "\"]}";
        //y creo un documento parseando ese string a formato Document
        Document findDocument = new Document(Document.parse(callingCodes));
        
        //Creo un documento en donde guardaré los resultados
        MongoCursor<Document> resultDocument = collection.find(findDocument).iterator();
        
        //Si el string que armé existe devuelvo true, si no existe quedará en false como
        //estaba al inicio de esta función
        if(resultDocument.hasNext()) existe = true;
        
        return existe;
    }
    
    
    public void bucarPaisesPorRegion(String region){
        
        //UNA FORMA DE HACERLO ES ESTA:

//////       //Traigo la base de datos correspondiente
//////        MongoDatabase db = mongoClient.getDatabase("laboratorio4Practico2");
//////        //Traigo la colección correspondiente de esa base de datos en la cual buscaré
//////        MongoCollection<Document> collection = db.getCollection("pais");
//////        
//////        //Creo un documento con los criterios de búsqueda
//////        //En este caso hago un String Json de lo que quiero buscar en la base de datos
//////        //(el código del país que está en un array llamado "callingCodes")
//////        String busqueda = "{\"region\":\"" + region + "\"}";
//////        //y creo un documento parseando ese string a formato Document
//////        Document findDocument = new Document(Document.parse(busqueda));
//////        
//////        //Creo un documento en donde guardaré los resultados
//////        MongoCursor<Document> resultDocument = collection.find(findDocument).iterator();
//////        
//////        while (resultDocument.hasNext()){
//////            System.out.println(resultDocument.next());
//////        }
//////        
        
        //OTRA FORMA DE HACERLO TAL VEZ MÁS FÁCIL ES ESTA:
        //Traigo la base de datos
        MongoDatabase db = mongoClient.getDatabase("laboratorio4Practico2");
        //A qué colección accederé
        MongoCollection collection = db.getCollection("pais");
        //Creo un objeto (que termina siendo como un documento Mongo) BasicDBObjet
        BasicDBObject objetoConsulta = new BasicDBObject();
        //Le agrego una llave:valor
        objetoConsulta.put("region", region);
        
        //Uso MongoCursor que será un array de documentos con la búsqueda realizada
        MongoCursor<Document> resultados = collection.find(objetoConsulta).iterator();
        //Muestro por pantalla los resultados
        while (resultados.hasNext()) {
            System.out.println(resultados.next());
        }
        

    }
    
    public void paisesPorRegionYMayorPoblacion(String region, long poblacion){
        //Traigo la base de datos
        MongoDatabase db = mongoClient.getDatabase("laboratorio4Practico2");
        //A qué colección accederé
        MongoCollection collection = db.getCollection("pais");
        //Creo un objeto (que termina siendo como un documento Mongo) BasicDBObjet
        BasicDBObject filtro = new BasicDBObject();
        //Le agrego una llave:valor
        filtro.put("region", region);
        //Le agrego otra llave pero como valor le agrego un nuevo BasicDBObject que como llave
        //le pongo el operador $gt que significa "mayor a" y le paso la poblacion a la que tiene que ser mayor
        filtro.put("population", new BasicDBObject("$gt",poblacion));
        //Uso MongoCursor que será un array de documentos con la búsqueda realizada
        MongoCursor<Document> cursor = collection.find(filtro).iterator();
        //Muestro por pantalla los resultados
        while (cursor.hasNext()){
          System.out.println(cursor.next());
        }
    }
    
    public void bucarPaisesQueNoSonDeUnaRegion(String region){
        //Traigo la base de datos
        MongoDatabase db = mongoClient.getDatabase("laboratorio4Practico2");
        //A qué colección accederé
        MongoCollection collection = db.getCollection("pais");
        //Creo un objeto (que termina siendo como un documento Mongo) BasicDBObjet
        BasicDBObject filtro = new BasicDBObject();
        //Le agrego una llave:valor pero al valor creo un nuevo BasicDBObject
        //y uso el operador $ne que descarta los que son de esa region
        filtro.put("region", new BasicDBObject("$ne",region));
        
        //Uso MongoCursor que será un array de documentos con la búsqueda realizada
        MongoCursor<Document> cursor = collection.find(filtro).iterator();
        //Muestro por pantalla los resultados
        while (cursor.hasNext()){
          System.out.println(cursor.next());
        }
    }
    
    public void actualizarNombrePaisYPoblacion(String paisABuscar, String nuevoNombrePais, long nuevaCantidadPoblacion){
        //Traigo la base de datos
        MongoDatabase db = mongoClient.getDatabase("laboratorio4Practico2");
        //A qué colección accederé
        MongoCollection collection = db.getCollection("pais");
        
        //Ya ni sé cómo llegué a que esto funcione, pero funciona así:
        collection.updateOne(Document.parse("{name:'" + paisABuscar + "'}"), Document.parse("{$set:{name:'" + nuevoNombrePais + "'}}"));
        collection.updateOne(Document.parse("{name:'" + nuevoNombrePais + "'}"), Document.parse("{$set:{population:'" + nuevaCantidadPoblacion + "'}}"));

    }
    
    public void eliminarPais(int codePais){
        //Traigo la base de datos correspondiente
        MongoDatabase db = mongoClient.getDatabase("laboratorio4Practico2");
        //Traigo la colección correspondiente de esa base de datos en la cual buscaré
        MongoCollection<Document> collection = db.getCollection("pais");
        
        //Creo un documento con los criterios de búsqueda
        //En este caso hago un String Json de lo que quiero buscar en la base de datos
        //(el código del país que está en un array llamado "callingCodes")
        String callingCodes = "{\"callingCodes\":[\"" + codePais + "\"]}";
        //y creo un documento parseando ese string a formato Document
        Document findDocument = new Document(Document.parse(callingCodes));
        collection.deleteOne(findDocument);
    }
    
    public void paisesConPoblacionEntreDosNumeros(long numeroMenor, long numeroMayor){
        //Traigo la base de datos correspondiente
        MongoDatabase db = mongoClient.getDatabase("laboratorio4Practico2");
        //Traigo la colección correspondiente de esa base de datos en la cual buscaré
        MongoCollection<Document> collection = db.getCollection("pais");
        
        //UNA FORMA DE HACERLO ES ESTA
//////        String busqueda = "{population:{$gt:50000000, $lt:150000000}}";
//////        
//////        //Uso MongoCursor que será un array de documentos con la búsqueda realizada
//////        MongoCursor<Document> cursor = collection.find(Document.parse(busqueda)).iterator();
//////        //Muestro por pantalla los resultados
        
        //OTRA FORMA DE HACERLO ES ESTA
        
        //Hago un filtro que recibe gracias a and dos comparativos (gt y lt)
        Bson filter = and(gt("population", 50000000L), lt("population", 150000000L));
        //Puedo decirles cómo ordenarlo (en este caso con 1L es de menor a mayor en population
        Bson sort = eq("population", 1L);
        //con esto le pongo qué buscar, cómo ordenarlo y lo hago iterator
        MongoCursor<Document> cursor = collection.find(filter).sort(sort).iterator();
        
        
        while (cursor.hasNext()){
          System.out.println(cursor.next());
        }
    }
    
    public void mostrarPaisesOrdenadosPorNombre(){
        //Traigo la base de datos correspondiente
        MongoDatabase db = mongoClient.getDatabase("laboratorio4Practico2");
        //Traigo la colección correspondiente de esa base de datos en la cual buscaré
        MongoCollection<Document> collection = db.getCollection("pais");
        
        //Puedo decirles cómo ordenarlo (en este caso con 1L es de menor a mayor en name
        Bson sort = eq("name", 1L);
        //con esto le pongo que busque todo, cómo ordenarlo y lo hago iterator
        MongoCursor<Document> cursor = collection.find().sort(sort).iterator();
        
        while (cursor.hasNext()){
          System.out.println(cursor.next());
        }
    }
    
    public void mostrarPaisesSaltandoLosPrimerosNPaises(int num){
        //Traigo la base de datos correspondiente
        MongoDatabase db = mongoClient.getDatabase("laboratorio4Practico2");
        //Traigo la colección correspondiente de esa base de datos en la cual buscaré
        MongoCollection<Document> collection = db.getCollection("pais");
        
        //Puedo decirles cómo ordenarlo (en este caso con 1L es de menor a mayor en name
        Bson sort = eq("name", 1L);
        //con esto le pongo que busque todo, cómo ordenarlo, cuántos registros a saltar y lo hago iterator
        MongoCursor<Document> cursor = collection.find().sort(sort).skip(num).iterator();
        
        while (cursor.hasNext()){
          System.out.println(cursor.next());
        }
    }
    
    public void usarCreateIndex(){
        //Traigo la base de datos correspondiente
        MongoDatabase db = mongoClient.getDatabase("laboratorio4Practico2");
        //Traigo la colección correspondiente de esa base de datos en la cual buscaré
        MongoCollection<Document> collection = db.getCollection("pais");
        //Creamos el índice a partir de los callingCodes;
        collection.createIndex(Document.parse("{callingCodes:1}"));
        
    }
    
    
}
