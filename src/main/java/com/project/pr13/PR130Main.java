package com.project.pr13;

import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.project.pr13.format.PersonaFormatter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;


/**
 * Classe principal que gestiona la lectura i el processament de fitxers XML per obtenir dades de persones.
 * 
 * Aquesta classe s'encarrega de llegir un fitxer XML que conté informació de persones,
 * processar-lo i mostrar les dades formatades per consola.
 */
public class PR130Main {

    private final File dataDir;

    /**
     * Constructor de la classe PR130Main.
     * 
     * @param dataDir Directori on es troben els fitxers de dades.
     */
    public PR130Main(File dataDir) {
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

        PR130Main app = new PR130Main(dataDir);
        app.processarFitxerXML("persones.xml");
    }

    /**
     * Processa un fitxer XML per obtenir la informació de les persones i imprimir-la.
     * 
     * @param filename Nom del fitxer XML a processar.
     */
    public void processarFitxerXML(String filename) {
        File inputFile = new File(dataDir, filename);
        Document doc = parseXML(inputFile);
        if (doc != null) {
            NodeList persones = doc.getElementsByTagName("persona");
            imprimirCapçaleres();
            imprimirDadesPersones(persones);
        }
    }

    /**
     * Llegeix un fitxer XML i el converteix en un objecte Document.
     * 
     * @param inputFile Fitxer XML a llegir.
     * @return Document XML carregat o null si hi ha hagut un error en la lectura.
     */
    public static Document parseXML(File inputFile) {
        // *************** CODI PRÀCTICA **********************/
        try {
            // Crea una factoria per poder construir analitzadors de documents XML
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

            // Utilitza la factoria per crear un objecte DocumentBuilder (l'analitzador real)
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            // Analitza (parseja) el fitxer XML de l'ordinador i genera l'arbre DOM en memòria
            Document doc = dBuilder.parse(inputFile);

            // Normalitza l'estructura del document (elimina nodes de text buits o fragments separats)
            // Això assegura que les dades es llegeixin de forma consistent
            doc.getDocumentElement().normalize();

            // Retorna el document XML completament preparat per ser llegit
            return doc;

        } catch (Exception e) {
            // Si hi ha qualsevol error (fitxer no trobat, XML mal format, etc.), mostra l'error
            e.printStackTrace();
            // Retorna null per indicar que no s'ha pogut processar el fitxer
            return null;
        }
    }

    /**
     * Imprimeix per consola les capçaleres de la taula de persones.
     * Cal fer servir PersonaFormatter.getCapçaleres().
     */
    public static void imprimirCapçaleres() {
        // *************** CODI PRÀCTICA **********************/
        // Crida al mètode estàtic de PersonaFormatter per obtenir el text de les capçaleres 
        // alineades (Nom, Cognom, Edat, Ciutat i els guionets) i el mostra per pantalla.
        System.out.println(PersonaFormatter.getCapçaleres());
    }

    /**
     * Imprimeix per consola una línia per a cada persona del NodeList,
     * amb el format de PersonaFormatter.formatarPersona().
     *
     * @param persones NodeList amb els elements "persona" del document XML.
     */
    public static void imprimirDadesPersones(NodeList persones) {
        // *************** CODI PRÀCTICA **********************/
        // Recorre tota la llista de nodes rebuts utilitzant la longitud de la llista (getLength())
        for (int i = 0; i < persones.getLength(); i++){
            
            // Obté el node actual de la llista segons la posició 'i'
            Node nodePersona = persones.item(i);
            
            // Comprova que el node realment sigui un element XML (una etiqueta amb dades) 
            // i no un espai en blanc o un comentari de text dins de l'arxiu
            if (nodePersona.getNodeType() == Node.ELEMENT_NODE){
                
                // Fa un "cast" (conversió) de Node a Element per poder fer servir mètodes de cerca d'etiquetes
                Element elm = (Element) nodePersona;
                
                // Busca l'etiqueta <nom> de la persona, agafa la primera que troba (item(0)) i en llegeix el text
                NodeList nodeNom = elm.getElementsByTagName("nom");
                String nom = nodeNom.item(0).getTextContent();
                
                // Busca l'etiqueta <cognom> de la persona, agafa la primera i en llegeix el text
                NodeList nodeCognom = elm.getElementsByTagName("cognom");
                String cognom = nodeCognom.item(0).getTextContent();
                
                // Busca l'etiqueta <edat> de la persona, agafa la primera i en llegeix el text
                NodeList nodeEdat = elm.getElementsByTagName("edat");
                String edat = nodeEdat.item(0).getTextContent();
                
                // Busca l'etiqueta <ciutat> de la persona, agafa la primera i en llegeix el text
                NodeList nodeCiutat = elm.getElementsByTagName("ciutat");
                String ciutat = nodeCiutat.item(0).getTextContent();

                // Envia els 4 textos extrets al formatejador per crear la línia amb les columnes 
                // perfectament alineades, i immediatament la imprimeix per pantalla.
                System.out.println(PersonaFormatter.formatarPersona(nom, cognom, edat, ciutat));
            } 
        }
    }

}
