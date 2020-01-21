
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
    static final int NB_BACS = 4; //Constante du nombre de bacs relie au Raspberry pi.
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
        
        //Create gpio controller
        GpioController gpio = GpioFactory.getInstance();
        
        //Provision gpio pin 1,3,5,7 as an output pin
        t_outputIOs[0] = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "LED1", PinState.LOW);
        t_outputIOs[1] = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, "LED2", PinState.LOW);
        t_outputIOs[2] = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05, "LED3", PinState.LOW);
        t_outputIOs[3] = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_07, "LED4", PinState.LOW);
        
        //Provision gpio pin 0,2,4,6 as an input pin with its internal pull down resistor enabled
        t_inputIOs[0] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_00, "Bac1", PinPullResistance.PULL_UP);
        t_inputIOs[1] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, "Bac2", PinPullResistance.PULL_UP);
        t_inputIOs[2] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_04, "Bac3", PinPullResistance.PULL_UP);
        t_inputIOs[3] = gpio.provisionDigitalInputPin(RaspiPin.GPIO_06, "Bac4", PinPullResistance.PULL_UP);
        
        
        String user = "admin";
        String password = "admin";
        String host = "localhost"; // Possiblement a modifier***********************************************************************************************
        int port = Integer.parseInt("1883");
        final String destination = "/scal/scal_reponse_requete"; // A modifier*************************************************************************************
        
        JSONObject initBaseJsonObj = new JSONObject();
        /*initBaseJsonObj.put("Base", "Debut de la communication"); //Note: Trouver comment mettre des accents*****
        initBaseJsonObj.put("Base2", "Suivez attentivement les instructions de votre guide a l'etape indiquee");
        initBaseJsonObj.put("Base3", "Commence avec l'etape numero 1");*/
        
        //Note: Trouver comment mettre des accents*******************************************************************************************
        initBaseJsonObj.put("Base", new String[] { "Debut de la communication", "Suivez attentivement les instructions de votre guide a l'etape indiquee", "Commence avec l'etape numero 1"});
        
        MQTT mqtt = new MQTT();
        mqtt.setHost(host, port);
        mqtt.setUserName(user);
        mqtt.setPassword(password);
        
        BlockingConnection connection = mqtt.blockingConnection();
        connection.connect();
        
        String TOPIC_REPONSE = "/scal/scal_requete_acces";
        
        /* *********************Publishing du message d'initialisation******************* */
        String DATA = initBaseJsonObj.toString(3);
        Buffer msg = new AsciiBuffer(DATA);
        
        UTF8Buffer topic = new UTF8Buffer(destination);
        connection.publish(topic, msg, QoS.AT_LEAST_ONCE, false);
        
        /* **********************Preparation de la publication du message d'erreur de selection des bacs********************** */
        JSONObject erreurBaseJsonObj = new JSONObject();
        
        while(true)
        {
            try
            {
                t_outputIOs[numPageCourante-1].high();
                
                if(t_inputIOs[0].isHigh())
                {
                    if(numPageCourante != 1)
                    {
                        erreurBaseJsonObj.put("Base", new String[] { "Ce n'est pas le bon bac!!!", "Allez a l'etape numero " + numPageCourante });
                        DATA = erreurBaseJsonObj.toString(2);
                        Buffer msgErreur = new AsciiBuffer(DATA);
                        connection.publish(topic, msgErreur, QoS.AT_LEAST_ONCE, false);
                    }
                }
                else if(t_inputIOs[1].isHigh())
                {
                    if(numPageCourante != 2)
                    {
                        erreurBaseJsonObj.put("Base", new String[] { "Ce n'est pas le bon bac!!!", "Allez a l'etape numero " + numPageCourante });
                        DATA = erreurBaseJsonObj.toString(2);
                        Buffer msgErreur = new AsciiBuffer(DATA);
                        connection.publish(topic, msgErreur, QoS.AT_LEAST_ONCE, false);
                    }
                }
                else if(t_inputIOs[2].isHigh())
                {
                    if(numPageCourante != 3)
                    {
                        erreurBaseJsonObj.put("Base", new String[] { "Ce n'est pas le bon bac!!!", "Allez a l'etape numero " + numPageCourante });
                        DATA = erreurBaseJsonObj.toString(2);
                        Buffer msgErreur = new AsciiBuffer(DATA);
                        connection.publish(topic, msgErreur, QoS.AT_LEAST_ONCE, false);
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
                }
            }
            catch(Exception e)
            {
                
            }
        }
                
    }
}
