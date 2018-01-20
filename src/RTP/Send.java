package RTP;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.CannotRealizeException;
import javax.media.CaptureDeviceInfo;
import javax.media.CaptureDeviceManager;
import javax.media.Controller;
import javax.media.DataSink;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoDataSinkException;
import javax.media.NoDataSourceException;
import javax.media.NoPlayerException;
import javax.media.NoProcessorException;
import javax.media.NotRealizedError;
import javax.media.Player;
import javax.media.Processor;
import javax.media.ProcessorModel;
import javax.media.control.FormatControl;
import javax.media.control.TrackControl;
import javax.media.format.AudioFormat;
import javax.media.format.VideoFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;


public class Send {
    private static final Format[] FORMATS = new Format[]{new AudioFormat(AudioFormat.ULAW_RTP)}; //Ulaw_RTP
    private static final ContentDescriptor CONTENT_DESCRIPTOR = new ContentDescriptor(ContentDescriptor.RAW_RTP);
    private DataSink dataSink;

    public Send(String ipDest) {
        try{
            // media source = microphone
            MediaLocator locator = new MediaLocator("javasound://0");

            // creating a source that will be used in creating the processor
            DataSource source = Manager.createDataSource(locator);

            //creating the processor form the source and formats that we want (RTP)
            Processor mediaProcessor = Manager.createRealizedProcessor(new ProcessorModel(source, FORMATS, CONTENT_DESCRIPTOR));

            // this is the output medialocator : ip, port and data type  //to
            MediaLocator outputMediaLocator = new MediaLocator("rtp://"+ ipDest +":10000/audio");

            // now , we are creating a datasink from the processor's output datasource and send it to output locator
            dataSink = Manager.createDataSink(mediaProcessor.getDataOutput(), outputMediaLocator);

            // start the processor
            mediaProcessor.start();
            System.out.println("Transmiting...");
        }
        catch (Exception ex){
            ex.getStackTrace();
        }
    }

    public void open() throws IOException, SecurityException {
        dataSink.open();
    }

    public void start() throws IOException {
        //start streaming the RTP data
        dataSink.start();
    }

    public void close() {
        dataSink.close();
    }
}

