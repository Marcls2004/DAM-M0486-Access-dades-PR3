package com.project.pr13;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.project.pr13.format.AsciiTablePrinter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Classe principal que permet gestionar un fitxer XML de cursos amb opcions per llistar, afegir i eliminar alumnes, 
 * així com mostrar informació dels cursos i mòduls.
 * 
 * Aquesta classe inclou funcionalitats per interactuar amb un fitxer XML, executar operacions de consulta,
 * i realitzar modificacions en el contingut del fitxer.
 */
public class PR132Main {

    private final Path xmlFilePath;
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Constructor de la classe PR132Main.
     * 
     * @param xmlFilePath Ruta al fitxer XML que conté la informació dels cursos.
     */
    public PR132Main(Path xmlFilePath) {
        this.xmlFilePath = xmlFilePath;
    }

    /**
     * Mètode principal que inicia l'execució del programa.
     * 
     * @param args Arguments passats a la línia de comandament (no s'utilitzen en aquest programa).
     */
    public static void main(String[] args) {
        String userDir = System.getProperty("user.dir");
        Path xmlFilePath = Paths.get(userDir, "data", "pr13", "cursos.xml");

        PR132Main app = new PR132Main(xmlFilePath);
        app.executar();
    }

    /**
     * Executa el menú principal del programa fins que l'usuari decideixi sortir.
     */
    public void executar() {
        boolean exit = false;
        while (!exit) {
            mostrarMenu();
            System.out.print("Escull una opció: ");
            int opcio = scanner.nextInt();
            scanner.nextLine(); // Netegem el buffer del scanner
            exit = processarOpcio(opcio);
        }
    }

    /**
     * Processa l'opció seleccionada per l'usuari.
     * 
     * @param opcio Opció seleccionada al menú.
     * @return True si l'usuari decideix sortir del programa, false en cas contrari.
     */
    public boolean processarOpcio(int opcio) {
        String cursId;
        String nomAlumne;
        switch (opcio) {
            case 1:
                List<List<String>> cursos = llistarCursos();
                imprimirTaulaCursos(cursos);
                return false;
            case 2:
                System.out.print("Introdueix l'ID del curs per veure els seus mòduls: ");
                cursId = scanner.nextLine();
                List<List<String>> moduls = mostrarModuls(cursId);
                imprimirTaulaModuls(moduls);
                return false;
            case 3:
                System.out.print("Introdueix l'ID del curs per veure la llista d'alumnes: ");
                cursId = scanner.nextLine();
                List<String> alumnes = llistarAlumnes(cursId);
                imprimirLlistaAlumnes(alumnes);
                return false;
            case 4:
                System.out.print("Introdueix l'ID del curs on vols afegir l'alumne: ");
                cursId = scanner.nextLine();
                System.out.print("Introdueix el nom complet de l'alumne a afegir: ");
                nomAlumne = scanner.nextLine();
                afegirAlumne(cursId, nomAlumne);
                return false;
            case 5:
                System.out.print("Introdueix l'ID del curs on vols eliminar l'alumne: ");
                cursId = scanner.nextLine();
                System.out.print("Introdueix el nom complet de l'alumne a eliminar: ");
                nomAlumne = scanner.nextLine();
                eliminarAlumne(cursId, nomAlumne);
                return false;
            case 6:
                System.out.println("Sortint del programa...");
                return true;
            default:
                System.out.println("Opció no reconeguda. Si us plau, prova de nou.");
                return false;
        }
    }

    /**
     * Mostra el menú principal amb les opcions disponibles.
     */
    private void mostrarMenu() {
        System.out.println("\nMENÚ PRINCIPAL");
        System.out.println("1. Llistar IDs de cursos i tutors");
        System.out.println("2. Mostrar IDs i títols dels mòduls d'un curs");
        System.out.println("3. Llistar alumnes d’un curs");
        System.out.println("4. Afegir un alumne a un curs");
        System.out.println("5. Eliminar un alumne d'un curs");
        System.out.println("6. Sortir");
    }

    /**
     * Llegeix el fitxer XML i llista tots els cursos amb el seu tutor i nombre d'alumnes.
     * 
     * @return Llista amb la informació dels cursos (ID, tutor, nombre d'alumnes).
     */
    public List<List<String>> llistarCursos() {
        // *************** CODI PRÀCTICA **********************/
        try {
            // Inicialitza la llista de llistes on es guardaran les files de la taula final
            List<List<String>> listado = new ArrayList<>();
            // Carrega l'arxiu XML del disc a la memòria RAM
            Document document = carregarDocumentXML(xmlFilePath);
            // Instancia el motor de cerques XPath
            XPath xPath = XPathFactory.newInstance().newXPath();
            
            // Executa l'expressió XPath per obtenir tots els nodes <curs> de l'arxiu XML
            NodeList cursos = (NodeList) xPath.evaluate("//curs", document, XPathConstants.NODESET);

            // Recorre cada node de curs trobat
            for (int i = 0; i < cursos.getLength(); i++) {
                Node curs = cursos.item(i);
                
                // Extreu el valor de l'atribut 'id' del curs actual (@id)
                String id = (String) xPath.evaluate("@id", curs, XPathConstants.STRING);
                // Extreu el contingut de text de l'etiqueta <tutor> que està dins del curs
                String tutor = (String) xPath.evaluate("tutor", curs, XPathConstants.STRING);
                
                // Utilitza la funció count() d'XPath per comptar quants nodes <alumne> hi ha a l'etiqueta <alumnes>
                int totalAlumnes = ((Number) xPath.evaluate("count(alumnes/alumne)", curs, XPathConstants.NUMBER)).intValue();
                
                // Crea una llista per representar la fila actual de dades de la taula
                List<String> fila = new ArrayList<>();
                fila.add(id);
                fila.add(tutor);
                fila.add(String.valueOf(totalAlumnes)); // Converteix el número a text
                
                // Afegeix la fila al llistat final de cursos
                listado.add(fila);
            }

            // Retorna tota la informació recopilada per ser mostrada per pantalla
            return listado;
            
        } catch (XPathExpressionException e) {
            // Captura l'error de sintaxi d'XPath i llança una excepció detallada
            throw new RuntimeException("Error en processar l'expressió XPath.", e);
        }
    }

    /**
     * Imprimeix per consola una taula amb la informació dels cursos.
     * 
     * @param cursos Llista amb la informació dels cursos.
     */
    public void imprimirTaulaCursos(List<List<String>> cursos) {
        List<String> capçaleres = List.of("ID", "Tutor", "Total Alumnes");
        AsciiTablePrinter.imprimirTaula(capçaleres, cursos);
    }

    /**
     * Mostra els mòduls d'un curs especificat pel seu ID.
     * 
     * @param idCurs ID del curs del qual es volen veure els mòduls.
     * @return Llista amb la informació dels mòduls (ID, títol).
     */
    public List<List<String>> mostrarModuls(String idCurs) {
        // *************** CODI PRÀCTICA **********************/
        try {
            // Inicialitza la llista per guardar els mòduls trobats
            List<List<String>> listadoModuls = new ArrayList<>();
            // Carrega el fitxer XML de cursos
            Document document = carregarDocumentXML(xmlFilePath);
            // Instancia el motor XPath
            XPath xPath = XPathFactory.newInstance().newXPath();
            
            // Expressió XPath amb filtre: cerca el <curs> que coincideixi amb l'ID donat,
            // i d'aquest, agafa tots els elements <modul> viatjant per la ruta de nodes
            NodeList moduls = (NodeList) xPath.evaluate("//curs[@id='" + idCurs + "']/moduls/modul", document, XPathConstants.NODESET);

            // Recorre la llista de mòduls recuperats d'aquest curs especificat
            for (int i = 0; i < moduls.getLength(); i++) {
                Node modul = moduls.item(i);
                
                // Extreu l'atribut 'id' del mòdul actual
                String id = (String) xPath.evaluate("@id", modul, XPathConstants.STRING);
                // Extreu el contingut de text del node fill <titol> del mòdul
                String titol = (String) xPath.evaluate("titol", modul, XPathConstants.STRING);
                
                // Crea i omple la fila de dades del mòdul
                List<String> fila = new ArrayList<>();
                fila.add(id);
                fila.add(titol);
                
                // Afegeix el mòdul a la llista de resultats
                listadoModuls.add(fila);
            }

            // Retorna el llistat dels mòduls obtinguts per a aquest curs
            return listadoModuls;
            
        } catch (XPathExpressionException e) {
            throw new RuntimeException("Error en processar l'expressió XPath.", e);
        }
    }

    /**
     * Imprimeix per consola una taula amb la informació dels mòduls.
     * 
     * @param moduls Llista amb la informació dels mòduls.
     */
    public void imprimirTaulaModuls(List<List<String>> moduls) {
        List<String> capçaleres = List.of("ID Mòdul", "Títol");
        AsciiTablePrinter.imprimirTaula(capçaleres, moduls);
    }

    /**
     * Llista els alumnes inscrits en un curs especificat pel seu ID.
     * 
     * @param idCurs ID del curs del qual es volen veure els alumnes.
     * @return Llista amb els noms dels alumnes.
     */
    public List<String> llistarAlumnes(String idCurs) {
        // *************** CODI PRÀCTICA **********************/
        try {
            // Inicialitza la llista per emmagatzemar els noms dels alumnes
            List<String> listadoAlumnes = new ArrayList<>();
            // Carrega el document XML
            Document document = carregarDocumentXML(xmlFilePath);
            // Instancia l'objecte XPath
            XPath xPath = XPathFactory.newInstance().newXPath();
            
            // Executa la cerca per obtenir tots els nodes <alumne> que pertanyen al curs seleccionat
            NodeList alumnes = (NodeList) xPath.evaluate("//curs[@id='" + idCurs + "']/alumnes/alumne", document, XPathConstants.NODESET);

            // Recorre un per un els alumnes obtinguts de la cerca
            for (int i = 0; i < alumnes.getLength(); i++) {
                Node alumne = alumnes.item(i);
                // Extreu el text del node, que equival al nom de l'alumne
                String nomAlumne = alumne.getTextContent();
                // Afegeix el nom directament a la llista de cadenes
                listadoAlumnes.add(nomAlumne);
            }

            // Retorna el llistat de cadenes amb els noms dels alumnes del curs
            return listadoAlumnes;
            
        } catch (XPathExpressionException e) {
            throw new RuntimeException("Error en processar l'expressió XPath.", e);
        }
    }


    /**
     * Imprimeix per consola la llista d'alumnes d'un curs.
     * 
     * @param alumnes Llista d'alumnes a imprimir.
     */
    public void imprimirLlistaAlumnes(List<String> alumnes) {
        System.out.println("Alumnes:");
        alumnes.forEach(alumne -> System.out.println("- " + alumne));
    }

    /**
     * Afegeix un alumne a un curs especificat pel seu ID.
     * 
     * @param idCurs ID del curs on es vol afegir l'alumne.
     * @param nomAlumne Nom de l'alumne a afegir.
     */
    public void afegirAlumne(String idCurs, String nomAlumne) {
        // *************** CODI PRÀCTICA **********************/
        try {
            // Carrega el fitxer de dades XML
            Document document = carregarDocumentXML(xmlFilePath);
            XPath xPath = XPathFactory.newInstance().newXPath();
            
            // Busca el node pare <alumnes> del curs corresponent mitjançant XPath de tipus NODE (un sol node)
            Node alumnesNode = (Node) xPath.evaluate("//curs[@id='" + idCurs + "']/alumnes", document, XPathConstants.NODE);

            // Si s'ha trobat el node de la llista d'alumnes del curs...
            if (alumnesNode != null) {

                // Crea un nou tag XML buit anomenat <alumne> fent servir el constructor de la factoria del document
                Element nouAlumne = document.createElement("alumne");
                // Defineix el text de l'etiqueta amb el nom complet introduït per l'usuari
                nouAlumne.setTextContent(nomAlumne);
                // Insereix el nou element com a fill al final de la llista del node <alumnes>
                alumnesNode.appendChild(nouAlumne);

                // Desa permanentment les modificacions de l'estructura de dades al fitxer físic cursos.xml
                guardarDocumentXML(document);
                System.out.println("Alumne afegit amb èxit.");
            } else {
                // Missatge de control en cas que l'ID de curs no hagi retornat cap coincidència
                System.out.println("No s'ha trobat cap curs amb l'ID especificat.");
            }
        } catch (XPathExpressionException e) {
            throw new RuntimeException("Error en processar l'expressió XPath.", e);
        }
    }


    /**
     * Elimina un alumne d'un curs especificat pel seu ID.
     * 
     * @param idCurs ID del curs d'on es vol eliminar l'alumne.
     * @param nomAlumne Nom de l'alumne a eliminar.
     */
    public void eliminarAlumne(String idCurs, String nomAlumne) {
        // *************** CODI PRÀCTICA **********************/
        try {
            // Carrega l'arxiu XML
            Document document = carregarDocumentXML(xmlFilePath);
            XPath xPath = XPathFactory.newInstance().newXPath();
            
            // Busca de manera molt precisa la coincidència exacta d'un node <alumne> que es trobi
            // sota el curs indicat i el text del qual coincideixi exactament amb el nom de l'alumne
            Node alumnes = (Node) xPath.evaluate("//curs[@id='" + idCurs + "']/alumnes/alumne[text()='" + nomAlumne + "']", document, XPathConstants.NODE);

            // Si s'ha aconseguit trobar el node d'aquest alumne concret...
            if (alumnes != null){
                // Puja un nivell fins al node pare (<alumnes>) i li demana que elimini aquest fill en concret
                alumnes.getParentNode().removeChild(alumnes);
                
                // Desa l'estat actualitzat de l'arbre XML al fitxer físic
                guardarDocumentXML(document);
                System.out.println("Alumne eliminat amb èxit.");
            } else {
                // Alerta si l'alumne no s'ha trobat o no està matriculat en aquest curs
                System.out.println("No s'ha trobat cap alumne amb el nom especificat en aquest curs.");
            }
        } catch (XPathExpressionException e) {
            throw new RuntimeException("Error en processar l'expressió XPath.", e);
        }
    }


    /**
     * Carrega el document XML des de la ruta especificada.
     * 
     * @param pathToXml Ruta del fitxer XML a carregar.
     * @return Document XML carregat.
     */
    private Document carregarDocumentXML(Path pathToXml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(pathToXml.toFile());
        } catch (Exception e) {
            throw new RuntimeException("Error en carregar el document XML.", e);
        }
    }

    /**
     * Guarda el document XML proporcionat en la ruta del fitxer original.
     * 
     * @param document Document XML a guardar.
     */
    private void guardarDocumentXML(Document document) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            // Sense això, l'espai en blanc original es manté i el fitxer s'omple de línies buides
            eliminarEspaisEnBlanc(document);

            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(xmlFilePath.toFile());
            transformer.transform(source, result);
            System.out.println("El fitxer XML ha estat guardat amb èxit.");
        } catch (TransformerException e) {
            System.out.println("Error en guardar el fitxer XML.");
            e.printStackTrace();
        }
    }

    /**
     * Elimina, de manera recursiva, els nodes de text que només contenen espais, tabuladors
     * o salts de línia (l'indentació del fitxer original). Si no s'eliminen, en tornar a
     * escriure el document amb INDENT = "yes" el fitxer s'omple de línies en blanc.
     * És el mateix mètode trimWhitespace que s'explica a la teoria.
     *
     * @param node Node a partir del qual es fa la neteja (normalment, el Document).
     */
    private static void eliminarEspaisEnBlanc(Node node) {
        NodeList fills = node.getChildNodes();
        // Es recorre de darrere cap endavant perquè getChildNodes() és una llista «viva»
        for (int i = fills.getLength() - 1; i >= 0; i--) {
            Node fill = fills.item(i);
            if (fill.getNodeType() == Node.TEXT_NODE && fill.getTextContent().isBlank()) {
                node.removeChild(fill);
            } else {
                eliminarEspaisEnBlanc(fill);
            }
        }
    }
}
