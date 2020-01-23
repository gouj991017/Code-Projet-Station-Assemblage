
/**
 * Write a description of class AppBase here.
 * 
 * @author (Jeremy Goulet) 
 * @version (29-11-2019)
 */

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.*;

import org.fusesource.hawtbuf.AsciiBuffer;
import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;
import java.sql.*;

public class AppBase
{
    static final int NB_BACS = 8; //Constante du nombre de bacs relie au Raspberry pi.
    // instance variables - replace the example below with your own
    static GpioPinDigitalOutput t_outputIOs[] = new GpioPinDigitalOutput[NB_BACS];
    static GpioPinDigitalInput t_inputIOs[] = new GpioPinDigitalInput[NB_BACS];

    /**
     * Constructor for objects of class AppBase
     */
    public AppBase()
    {
        
    }

    /**
     * An example of a method - replace this comment with your own
     * 
     * @param  y   a sample parameter for a method
     * @return     the sum of x and y 
     */
    public static void main() throws Exception
    {
        int numPageCourante = 1;
        int choixProduit = 0;
        boolean exit = false;
        String Str = "";
        int choixBase = 0;
        int choixCrayon = 0;
        int ChoixSupports = 0;
        int couleur = 0;
        int quantite = 1;
        int i = 0;
        
        //Create gpio controller
        GpioController gpio = GpioFactory.getInstance();
        
        try
        {
        //Provision gpio pin 1,3,5,7 as an output pin
        t_outputIOs[0] = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "LED1", PinState.HIGH);   //pin 12 du header
        t_outputIOs[1] = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_09, "LED2", PinState.LOW);    //pin 5 du header
        t_outputIOs[2] = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_21, "LED3", PinState.LOW);    //pin 29 du header
        t_outputIOs[3] = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_11, "LED4", PinState.LOW);    //pin 26 du header
        
        //Provision gpio pin 0,2,4,6 as an input pin with its internal pull down resistor enabled
        t_inputIOs[0] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_30, "Bac1", PinPullResistance.OFF); //pin 27 du header
        t_inputIOs[1] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_00, "Bac2", PinPullResistance.OFF); //pin 11 du header
        t_inputIOs[2] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_07, "Bac3", PinPullResistance.OFF); //pin 7 du header
        t_inputIOs[3] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_22, "Bac4", PinPullResistance.OFF); //pin 31 du header
        }catch(Exception ex){}
        
        String user = "admin";
        String password = "admin";
        String host = "192.168.137.171"; // Possiblement a modifier  192.168.137.171********************************************************************************************
        int port = Integer.parseInt("1883");
        final String destination = "/scal/scal_reponse_requete"; // A modifier*************************************************************************************
        
        JSONObject initBaseJsonObj = new JSONObject();
        /*initBaseJsonObj.put("Base", "Debut de la communication"); //Note: Trouver comment mettre des accents*****
        initBaseJsonObj.put("Base2", "Suivez attentivement les instructions de votre guide a l'etape indiquee");
        initBaseJsonObj.put("Base3", "Commence avec l'etape numero 1");
        */
        //Note: Trouver comment mettre des accents*******************************************************************************************
        initBaseJsonObj.put("Base", new String[] { "Debut de la communication", "Suivez attentivement les instructions de votre guide a l'etape indiquee"});
        
        MQTT mqtt = new MQTT();
        mqtt.setHost(host, port);
        mqtt.setUserName(user);
        mqtt.setPassword(password);
        
        BlockingConnection connection = mqtt.blockingConnection();
        connection.connect();
        
        String TOPIC_REPONSE = "/scal/scal_requete_acces";
        
        // *********************Publishing du message d'initialisation******************* 
        String DATA = initBaseJsonObj.toString(3);
        Buffer msg = new AsciiBuffer(DATA);
        
        UTF8Buffer topic = new UTF8Buffer(destination);
        connection.publish(topic, msg, QoS.AT_LEAST_ONCE, false);
        
        // **********************Preparation de la publication du message d'erreur de selection des bacs********************** 
        JSONObject erreurBaseJsonObj = new JSONObject();
        JSONObject validationBaseJsonObj = new JSONObject();
        
        
        //Pin  28,11,13 non-controlable
        /*
        System.out.println(t_inputIOs[2].isHigh());
        System.out.println(t_inputIOs[2].isLow());
        System.out.println(t_inputIOs[3].isHigh());
        System.out.println(t_inputIOs[3].isLow());
        
        System.out.println(t_outputIOs[0].isHigh());
        System.out.println(t_outputIOs[0].isLow());
        System.out.println(t_outputIOs[3].isHigh());
        System.out.println(t_outputIOs[3].isLow());
        
        t_outputIOs[0].high();
        t_outputIOs[1].low();
        t_outputIOs[2].low();
        t_outputIOs[3].low();
        
        System.out.println(t_outputIOs[0].isHigh());
        System.out.println(t_outputIOs[1].isHigh());
        System.out.println(t_outputIOs[2].isHigh());
        System.out.println(t_outputIOs[3].isHigh());
        */
        //t_outputIOs[0].low();
        
        while(true)
        {
            Topic[] topics = {new Topic(TOPIC_REPONSE, QoS.AT_LEAST_ONCE)};
            connection.subscribe(topics);
            
            exit = false;
            while(!exit)
            {
                try
                {
                    Message message = connection.receive(150, TimeUnit.SECONDS);
                    
                    Str = new String(message.getPayload());
                    JSONObject messageJsonObject = new JSONObject(Str);
                    
                    choixProduit = messageJsonObject.getInt(""); //Choix du produit
                    choixBase = messageJsonObject.getInt("");    //Choix de la base
                    couleur = messageJsonObject.getInt("");      //Choix de la couleur
                    choixCrayon = messageJsonObject.getInt("");  //Choix porte Crayon
                    ChoixSupports = messageJsonObject.getInt(""); //Choix des supports
                    quantite = messageJsonObject.getInt("");     //Quantite
                    
                    t_outputIOs[numPageCourante].high(); //allume la LED du bac courant
                 
                    if(choixProduit == 1) //Porte-cellulaire
                    {
                        while(i < quantite)
                        {
                            if(t_inputIOs[0].isHigh())
                            {
                                while(t_inputIOs[0].isHigh());
                                if(numPageCourante != 1)
                                {
                                    erreurBaseJsonObj.put("Base", new String[] { "Ce n'est pas le bon bac!!!", "Allez a l'etape numero " + numPageCourante });
                                    DATA = erreurBaseJsonObj.toString(2);
                                    Buffer msgErreur = new AsciiBuffer(DATA);
                                    connection.publish(topic, msgErreur, QoS.AT_LEAST_ONCE, false);
                                }
                                else
                                {
                                    t_outputIOs[numPageCourante].low();
                                    numPageCourante++;
                                    validationBaseJsonObj.put("Step 1", "Allez au bac numero " + numPageCourante);
                                    DATA = validationBaseJsonObj.toString(1);
                                    Buffer msgValide = new AsciiBuffer(DATA);
                                    connection.publish(topic, msgValide, QoS.AT_LEAST_ONCE, false);
                                }
                            }
                            else if(t_inputIOs[1].isHigh())
                            {
                                while(t_inputIOs[1].isHigh());
                                if(numPageCourante != 2)
                                {
                                    erreurBaseJsonObj.put("Base", new String[] { "Ce n'est pas le bon bac!!!", "Allez a l'etape numero " + numPageCourante });
                                    DATA = erreurBaseJsonObj.toString(2);
                                    Buffer msgErreur = new AsciiBuffer(DATA);
                                    connection.publish(topic, msgErreur, QoS.AT_LEAST_ONCE, false);
                                }
                                else
                                {
                                    t_outputIOs[numPageCourante].low();
                                    numPageCourante = 1;    //Temporairement remis a 1.
                                    validationBaseJsonObj.put("Base", "Allez au bac numero " + numPageCourante);
                                    DATA = validationBaseJsonObj.toString(1);
                                    Buffer msgValide = new AsciiBuffer(DATA);
                                    connection.publish(topic, msgValide, QoS.AT_LEAST_ONCE, false);
                                    i++;
                                }
                            }
                            
                        }
                        i = 0;
                        exit = true;
                    }
                    else if(choixProduit == 2) //Porte-carte
                    {
                        while(i < quantite)
                        {
                            if(t_inputIOs[0].isHigh())
                            {
                                while(t_inputIOs[0].isHigh());
                                if(numPageCourante != 1)
                                {
                                    erreurBaseJsonObj.put("Step 1", new String[] { "Ce n'est pas le bon bac!!!", "Allez a l'etape numero " + numPageCourante });
                                    DATA = erreurBaseJsonObj.toString(2);
                                    Buffer msgErreur = new AsciiBuffer(DATA);
                                    connection.publish(topic, msgErreur, QoS.AT_LEAST_ONCE, false);
                                }
                                else
                                {
                                    t_outputIOs[numPageCourante].low();
                                    numPageCourante++;
                                    validationBaseJsonObj.put("Base", "Allez au bac numero " + numPageCourante);
                                    DATA = validationBaseJsonObj.toString(1);
                                    Buffer msgValide = new AsciiBuffer(DATA);
                                    connection.publish(topic, msgValide, QoS.AT_LEAST_ONCE, false);
                                }
                            }
                            else if(t_inputIOs[1].isHigh())
                            {
                                while(t_inputIOs[1].isHigh());
                                if(numPageCourante != 2)
                                {
                                    erreurBaseJsonObj.put("Base", new String[] { "Ce n'est pas le bon bac!!!", "Allez a l'etape numero " + numPageCourante });
                                    DATA = erreurBaseJsonObj.toString(2);
                                    Buffer msgErreur = new AsciiBuffer(DATA);
                                    connection.publish(topic, msgErreur, QoS.AT_LEAST_ONCE, false);
                                }
                                else
                                {
                                    t_outputIOs[numPageCourante].low();
                                    numPageCourante = 1;    //Temporairement remis a 1.
                                    validationBaseJsonObj.put("Base", "Allez au bac numero " + numPageCourante);
                                    DATA = validationBaseJsonObj.toString(1);
                                    Buffer msgValide = new AsciiBuffer(DATA);
                                    connection.publish(topic, msgValide, QoS.AT_LEAST_ONCE, false);
                                }
                            }
                            i++;
                        }
                        i = 0;
                        exit = true;
                    }
                    else
                    {
                        
                    }
                    
                    
                    /*
                    else if(t_inputIOs[2].isHigh())
                    {
                        if(numPageCourante != 3)
                        {
                            erreurBaseJsonObj.put("Base", new String[] { "Ce n'est pas le bon bac!!!", "Allez a l'etape numero " + numPageCourante });
                            DATA = erreurBaseJsonObj.toString(2);
                            Buffer msgErreur = new AsciiBuffer(DATA);
                            connection.publish(topic, msgErreur, QoS.AT_LEAST_ONCE, false);
                        }
                        else
                        {
                            numPageCourante++;
                            validationBaseJsonObj.put("Base", "Allez au bac numero " + numPageCourante);
                            DATA = validationBaseJsonObj.toString(1);
                            Buffer msgValide = new AsciiBuffer(DATA);
                            connection.publish(topic, msgValide, QoS.AT_LEAST_ONCE, false);
                        }
                    }
                    else if(t_inputIOs[3].isHigh())
                    {
                        if(numPageCourante != 4)
                        {
                            erreurBaseJsonObj.put("Base", new String[] { "Ce n'est pas le bon bac!!!", "Allez a l'etape numero " + numPageCourante });
                            DATA = erreurBaseJsonObj.toString(2);
                            Buffer msgErreur = new AsciiBuffer(DATA);
                            connection.publish(topic, msgErreur, QoS.AT_LEAST_ONCE, false);
                        }
                        else
                        {
                            numPageCourante++;
                            validationBaseJsonObj.put("Base", "Allez au bac numero " + numPageCourante);
                            DATA = validationBaseJsonObj.toString(1);
                            Buffer msgValide = new AsciiBuffer(DATA);
                            connection.publish(topic, msgValide, QoS.AT_LEAST_ONCE, false);
                        }
                    }
                    */
                }
                catch(Exception e)
                {
                    
                }
            }
        }
                
    }
}
