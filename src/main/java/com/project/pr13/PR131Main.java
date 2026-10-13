package com.project.pr13;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

/**
 * Classe principal que crea un document XML amb informació de llibres i el guarda en un fitxer.
 * 
 * Aquesta classe permet construir un document XML, afegir elements i guardar-lo en un directori
 * especificat per l'usuari.
 */
public class PR131Main {

    private File dataDir;

    /**
     * Constructor de la classe PR131Main.
     * 
     * @param dataDir Directori on es guardaran els fitxers de sortida.
     */
    public PR131Main(File dataDir) {
        this.dataDir = dataDir;
    }

    /**
     * Retorna el directori de dades actual.
     * 
     * @return Directori de dades.
     */
    public File getDataDir() {
        return dataDir;
    }

    /**
     * Actualitza el directori de dades.
     * 
     * @param dataDir Nou directori de dades.
     */
    public void setDataDir(File dataDir) {
        this.dataDir = dataDir;
    }

    /**
     * Mètode principal que inicia l'execució del programa.
     * 
     * @param args Arguments passats a la línia de comandament (no s'utilitzen en aquest programa).
     */
    public static void main(String[] args) {
        String userDir = System.getProperty("user.dir");
        File dataDir = new File(userDir, "data" + File.separator + "pr13");

        PR131Main app = new PR131Main(dataDir);
        app.processarFitxerXML("biblioteca.xml");
    }

    /**
     * Processa el document XML creant-lo, guardant-lo en un fitxer i comprovant el directori de sortida.
     * 
     * @param filename Nom del fitxer XML a guardar.
     */
    public void processarFitxerXML(String filename) {
        if (comprovarIDirCrearDirectori(dataDir)) {
            Document doc = construirDocument();
            File fitxerSortida = new File(dataDir, filename);
            guardarDocument(doc, fitxerSortida);
        }
    }

    /**
     * Comprova si el directori existeix i, si no és així, el crea.
     * 
     * @param directori Directori a comprovar o crear.
     * @return True si el directori ja existeix o s'ha creat amb èxit, false en cas contrari.
     */
    private boolean comprovarIDirCrearDirectori(File directori) {
        if (!directori.exists()) {
            return directori.mkdirs();
        }
        return true;
    }

    /**
     * Crea un document XML amb l'estructura d'una biblioteca i afegeix un llibre amb els seus detalls.
     * Cal construir-lo amb DocumentBuilder. No és privat perquè els tests el puguin comprovar.
     * 
     * @return Document XML creat o null en cas d'error.
     */
    static Document construirDocument() {
        // *************** CODI PRÀCTICA **********************/
        try {
            // Crea una factoria per poder obtenir un constructor de documents XML buits
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            
            // Crea el constructor de documents (l'eina per fabricar el nostre XML de zero)
            DocumentBuilder db = dbFactory.newDocumentBuilder();
            
            // Instancia un document XML totalment nou i buit a la memòria
            Document doc = db.newDocument();

            // Crea l'etiqueta arrel principal anomenada <biblioteca>
            Element biblioteca = doc.createElement("biblioteca");
            // Afegeix l'etiqueta <biblioteca> com el node principal del document XML
            doc.appendChild(biblioteca);

            // Crea l'etiqueta per a un llibre: <llibre>
            Element llibre = doc.createElement("llibre");
            // Li afegeix l'atribut id amb el valor "001" -> <llibre id="001">
            llibre.setAttribute("id", "001");
            // Fica el llibre dins de l'etiqueta biblioteca -> <biblioteca><llibre>...</llibre></biblioteca>
            biblioteca.appendChild(llibre);

            // Crea l'etiqueta <titol>, li assigna el text i la penja com a filla de <llibre>
            Element titol = doc.createElement("titol");
            titol.setTextContent("El viatge dels venturons");
            llibre.appendChild(titol);

            // Crea l'etiqueta <autor>, li assigna el text i la penja com a filla de <llibre>
            Element autor = doc.createElement("autor");
            autor.setTextContent("Joan Pla");
            llibre.appendChild(autor);

            // Crea l'etiqueta <anyPublicacio>, li assigna el text i la penja com a filla de <llibre>
            Element anyPublicacio = doc.createElement("anyPublicacio");
            anyPublicacio.setTextContent("1998");
            llibre.appendChild(anyPublicacio);

            // Crea l'etiqueta <editorial>, li assigna el text i la penja com a filla de <llibre>
            Element editorial = doc.createElement("editorial");
            editorial.setTextContent("Edicions Mar");
            llibre.appendChild(editorial);

            // Crea l'etiqueta <genere>, li assigna el text i la penja com a filla de <llibre>
            Element genere = doc.createElement("genere");
            genere.setTextContent("Aventura");
            llibre.appendChild(genere);

            // Crea l'etiqueta <pagines>, li assigna el text i la penja com a filla de <llibre>
            Element pagines = doc.createElement("pagines");
            pagines.setTextContent("320");
            llibre.appendChild(pagines);

            // Crea l'etiqueta <disponible>, li assigna el text i la penja com a filla de <llibre>
            Element disponible = doc.createElement("disponible");
            disponible.setTextContent("true");
            llibre.appendChild(disponible);

            // Retorna el document XML complet amb tota l'estructura que s'ha creat en memòria
            return doc;
            
        } catch (Exception e) {
            // Mostra per consola el rastre de l'error si falla la creació de l'analitzador XML
            e.printStackTrace();
            // Retorna null per indicar que el document no s'ha pogut construir correctament
            return null;
        }
    }

    /**
     * Guarda el document XML proporcionat en el fitxer especificat.
     * 
     * @param doc Document XML a guardar.
     * @param fitxerSortida Fitxer de sortida on es guardarà el document.
     */
    private static void guardarDocument(Document doc, File fitxerSortida) {
        // *************** CODI PRÀCTICA **********************/
        try {
            // Crea una factoria per poder generar objectes Transformer
            TransformerFactory transformerFactory = TransformerFactory.newInstance();

            // Crea un Transformer bàsic (objecte encarregat d'agafar dades en memòria i processar-les a fitxer)
            Transformer transformer = transformerFactory.newTransformer();

            // Defineix la font de dades d'origen, que en aquest cas és l'arbre DOM (el nostre document XML)
            DOMSource origen = new DOMSource(doc);

            // Defineix el lloc de destí on s'enviaran les dades (el fitxer físic on es guardarà)
            StreamResult desti = new StreamResult(fitxerSortida);

            // Executa la transformació: agafa el document XML de la memòria i l'escriu al fitxer de text real
            transformer.transform(origen, desti);
            
        } catch (TransformerException e) {
            // Captura i mostra qualsevol error específic que passi durant el procés d'escriptura o conversió
            e.printStackTrace();
        }
    }

}
