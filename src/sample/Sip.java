package sample;

import RTP.Receive;
import RTP.Send;

import javax.sdp.*;
import javax.sip.*;
import javax.sip.Dialog;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.header.*;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Sip implements SipListener {

    private Acceuil acceuil;

    // Objets utiles pour communiquer avec lâ€™API JAIN SIP.
    SipFactory sipFactory;            // Pour acceder Ã  lâ€™API SIP.
    SipStack sipStack;                // Le SIP stack.
    SipProvider sipProvider;          // Pour envoyer des messages SIP.
    MessageFactory messageFactory;   // Pour crÃ©er les messages SIP.
    HeaderFactory headerFactory;     // Pour crÃ©er les entÃªtes SIP.
    AddressFactory addressFactory;   // Pour crÃ©er les SIP URIs.
    ListeningPoint listeningPoint;    // SIP listening IP address/port.
    Properties properties;   //autres propriÃ©tÃ©s.


    // Objets pour stocker la configuration locale.
    String ip;                         // Adresse IP locale
    int port = (int)(Math.random() * (9000-8000)) + 8000;                  // Port local.
    String protocol = "udp";          // Protocole local de transport (UDP).
    int tag = (new Random()).nextInt();  // Le tag local.
    Address contactAddress;           // Lâ€™adresse de contact.
    ContactHeader contactHeader;     // Lâ€™entÃªte contact.

    private String transport;
    public SdpFactory sdpFactory;  //pour le corps du message SIP (SDP)

    public Sip(Acceuil acceuil) {
        this.acceuil = acceuil;
        Invite.sip = this;
    }


    public void onOpen(javafx.scene.control.TextField localAdr) {
        // A method called when you open your application.
        try {
            // Obtenir lâ€™adresse IP locale.
            this.ip = InetAddress.getLocalHost().getHostAddress();
            // CrÃ©er le SIP factory et affecter le path name.
            this.sipFactory = SipFactory.getInstance();
            this.sipFactory.setPathName("gov.nist");
            // CrÃ©er et configurer les propriÃ©tÃ©s du SIP stack
            this.properties = new Properties();
            this.properties.setProperty("javax.sip.STACK_NAME", "stack");
            // CrÃ©er le SIP stack.
            this.sipStack = this.sipFactory.createSipStack(this.properties);
            // CrÃ©er le message factory de SIP.
            this.messageFactory = this.sipFactory.createMessageFactory();
            // CrÃ©er le header factory de SIP.
            this.headerFactory = this.sipFactory.createHeaderFactory();
            // CrÃ©er lâ€™address factory de SIP.
            this.addressFactory = this.sipFactory.createAddressFactory();
            // CrÃ©er le SIP listening point et le lier Ã  lâ€™adresse IP locale, le port et
            //le protocole.

            this.listeningPoint = this.sipStack.createListeningPoint(this.ip, this.port, this.protocol);

            // CrÃ©er le SIP provider.
            this.sipProvider = this.sipStack.createSipProvider(this.listeningPoint);

            // Ajouter cette application comme SIP listener.
            this.sipProvider.addSipListener(this);

            // CrÃ©er lâ€™adresse de contacte utilisÃ©e pour tous les messages SIP.
            this.contactAddress = this.addressFactory.createAddress("sip:" + this.ip + ":" + this.port);
            // CrÃ©er le contact header utilisÃ© pour tous les messages SIP.
            this.contactHeader = this.headerFactory.createContactHeader(contactAddress);

            // Afficher lâ€™adresse IP locale et le port dans le text area.
            localAdr.setText("sip:" + this.ip + ":" + this.port);

        } catch (Exception e) {
            // Affichage de lâ€™erreur
        }

    }




    public void onInvite(javafx.scene.control.TextField destadr) {
        try {
            // CrÃ©er le To Header
            // Obtenir lâ€™adresse de destination Ã  partir du text field.
            Address addressTo = this.addressFactory.createAddress(destadr.getText());
            addressTo.setDisplayName("Alice");
            //par exemple
            ToHeader toHeader = headerFactory.createToHeader(addressTo, null);
            // CrÃ©er le request URI pour les messages SIP.
            javax.sip.address.URI requestURI = addressTo.getURI();
            // Affecter le type du protocole de Transport TCP ou UDP??
            transport = "udp";
            // CrÃ©er les Via Headers
            ArrayList viaHeaders = new ArrayList();
            ViaHeader viaHeader = this.headerFactory.createViaHeader(this.ip, this.port, transport, null);
            // ajouter les via headers
            viaHeaders.add(viaHeader);
            // CrÃ©er le ContentTypeHeader
            ContentTypeHeader contentTypeHeader = headerFactory.createContentTypeHeader("application", "sdp");

            // CrÃ©er une nouvelle entÃªte  CallId
            CallIdHeader callIdHeader = sipProvider.getNewCallId();

            // CrÃ©er une nouvelle entÃªte Cseq
            CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(1L, Request.INVITE);

            // CrÃ©er une nouvelle entÃªte MaxForwardsHeader
            MaxForwardsHeader maxForwards = headerFactory.createMaxForwardsHeader(70);

            // CrÃ©er le  "From" header.
            FromHeader fromHeader = this.headerFactory.createFromHeader(this.contactAddress, String.valueOf(this.tag));
            // CrÃ©er la requÃªte Invite.
            Request request = messageFactory.createRequest(requestURI, Request.INVITE,
                    callIdHeader, cSeqHeader, fromHeader,
                    toHeader, viaHeaders, maxForwards);
            // Ajouter lâ€™adresse de contacte.
            contactAddress.setDisplayName("Bousarehane");

            contactHeader = headerFactory.createContactHeader(contactAddress);
            request.addHeader(contactHeader);
            // CrÃ©er la transaction client.
            ClientTransaction inviteTid = this.sipProvider.getNewClientTransaction(request);

            // envoyer la requÃªte
            inviteTid.sendRequest();

            String sdpData = createSDPData(50002, 0); //create SDP content
            request.setContent(sdpData, contentTypeHeader);


            // Afficher le message dans le text area.
            System.out.println("InviteRequest sent:\n" + request.toString() + "\n\n");
            SwitchWindow.incomingCall(destadr.getText(), true);
            /*
            ControllerHome.send=new Send(destadr.getText().split(":")[1]);
            ControllerHome.send.open();
            ControllerHome.send.start();*/

        } catch (Exception e) {
            //Afficher lâ€™erreur en cas de problÃ¨me.
            System.out.println("InviteRequest sent failed: " + e.getMessage() + "\n");
        }



    }
    public String createSDPData(int localBasePort, int remoteBasePort) {
        try {
            sdpFactory = SdpFactory.getInstance();

            SessionDescription sessDescr = sdpFactory.createSessionDescription();
            String myIPAddr;
            try {
                myIPAddr = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException Uhe) {
                myIPAddr = "127.0.0.1";
            }       //"v=0"
            Version v = sdpFactory.createVersion(0);
//o=
            Origin o = sdpFactory.createOrigin("1234", 0, 0, "IN", "IP4", myIPAddr);
//"s=-"
            SessionName s = sdpFactory.createSessionName("-");
//c=
            Connection c = sdpFactory.createConnection("IN", "IP4", myIPAddr);
//"t=0 0"
            TimeDescription t = sdpFactory.createTimeDescription();
            Vector timeDescs = new Vector();
            timeDescs.add(t);

            // -------- Description du media Audio
            String[] formats = {"0", "4", "18"};
            MediaDescription am = sdpFactory.createMediaDescription("audio", localBasePort, 1, "RTP/AVP", formats);
//"m=video 22222 RTP/AVP 34";
            String[] vformats = {"34"};
            MediaDescription vm = sdpFactory.createMediaDescription("video", remoteBasePort, 1, "RTP/AVP", vformats);
            Vector mediaDescs = new Vector();

            mediaDescs.add(am);
            mediaDescs.add(vm);
            sessDescr.setVersion(v);
            sessDescr.setOrigin(o);
            sessDescr.setConnection(c);
            sessDescr.setSessionName(s);
            sessDescr.setTimeDescriptions(timeDescs);

            if (mediaDescs.size() > 0) {
                sessDescr.setMediaDescriptions(mediaDescs);
            }

            return sessDescr.toString();
        } catch (SdpException exc) {
            System.out.println("An SDP exception occurred while generating sdp description");
            exc.printStackTrace();
        }
        return "No SDP set";
    }


    public void processRequest(RequestEvent requestEvent) {

        // Get the request.
        Request request = requestEvent.getRequest();
        String descDest = ((FromHeader)request.getHeader("From")).getAddress().toString();
        System.out.println("RECV " + request.getMethod() + " " + descDest);
        Response response;
        try {
            // Get or create the server transaction.
            ServerTransaction transaction = requestEvent.getServerTransaction();
            if(null == transaction) {
                transaction = this.sipProvider.getNewServerTransaction(request);
            }

            // Update the SIP message table.
            if(request.getMethod().equals("INVITE")){

                SwitchWindow.incomingCall(descDest, false);
                while (Invite.isAccepted == null)
                    Thread.sleep(100);
                if(Invite.isAccepted){
                    System.out.println("Accept Invite");
                    // If the request is an INVITE & we accepted
                    response = this.messageFactory.createResponse(200, request);
                    ((ToHeader)response.getHeader("To")).setTag(String.valueOf(this.tag));
                    response.addHeader(this.contactHeader);
                    transaction.sendResponse(response);
                    System.out.println("SENT " + response.getStatusCode() + " " + response.getReasonPhrase());
                    Acceuil.send=new Send(descDest.split(":")[1]);
                    Acceuil.send.open();
                    Acceuil.send.start();

                }
                else{
                    System.out.println("Decline Invite");
                    return;
                }


            }

            // Process the request and send a response.

            /*if(request.getMethod().equals("REGISTER") || request.getMethod().equals("INVITE") || request.getMethod().equals("BYE")) {
                // If the request is a REGISTER or an INVITE or a BYE.
                response = this.messageFactory.createResponse(200, request);
                ((ToHeader)response.getHeader("To")).setTag(String.valueOf(this.tag));
                response.addHeader(this.contactHeader);
                transaction.sendResponse(response);
                System.out.println(" / SENT " + response.getStatusCode() + " " + response.getReasonPhrase());
            }*/

            else if(request.getMethod().equals("ACK")) {
                System.out.println("**ACK");
            }

        }
        catch(SipException e) {
            System.out.println("ERROR (SIP): " + e.getMessage());
        }
        catch(Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    Dialog dialog;

    @Override
    public void processResponse(ResponseEvent responseEvent) {
        // A method called when you receive a SIP response.
        // Obtenir le message reponse.
        // Get the request.
        Response response = responseEvent.getResponse();
        String descDest = ((FromHeader)response.getHeader("From")).getAddress().toString();
        System.out.println("RECV " + response.getStatusCode() + " " + descDest);
        if(response.getStatusCode() == Response.OK){
            SwitchWindow.controllerInvite.onAccept(null);
            try{
                dialog = responseEvent.getClientTransaction().getDialog();
                Request request = dialog.createAck(((CSeqHeader)response.getHeader("CSeq")).getSeqNumber());
                response.setHeader(contactHeader);
                dialog.sendAck(request);

                Acceuil.receive=new Receive(this.ip);
                Acceuil.receive.start();



            }catch(Exception e) {
                e.getStackTrace();
            }
        }
        // Afficher le message rÃ©ponse dans le text area.
        System.out.println("\nReceived response: " + response.toString());


    }
    public void onBye() {
        try {
            // A method called when you click on the "Bye" button.
            CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(1L, Request.BYE);

            Request request = this.dialog.createRequest("BYE");
            ClientTransaction transaction = this.sipProvider.getNewClientTransaction(request);
            this.dialog.sendRequest(transaction);
            try{
                Acceuil.send.close();
            }
            catch (Exception e){

            }
            try {
                Acceuil.receive.close();
            }
            catch (Exception ex){

            }
            SwitchWindow.goBack();

        } catch (SipException ex) {
            Logger.getLogger(Sip.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(Sip.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidArgumentException ex) {
            Logger.getLogger(Sip.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    @Override
    public void processTimeout(TimeoutEvent timeoutEvent) {

    }

    @Override
    public void processIOException(IOExceptionEvent ioExceptionEvent) {

    }

    @Override
    public void processTransactionTerminated(TransactionTerminatedEvent transactionTerminatedEvent) {

    }

    @Override
    public void processDialogTerminated(DialogTerminatedEvent dialogTerminatedEvent) {

    }

}
