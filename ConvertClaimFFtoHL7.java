/*This Program reads the inbound Claims File in the Covisint Standard Claims File
 *Format and convert the record into an HL7 ADT^A08 message to so that we can
 *create the patients before processing the inbound claims.
 */
package convertclaimfftohl7;
import java.io.*;
import java.util.*;

/**Author:  Sean Thomas Gibson
 * Date Written: 16-Apr-2015
 */
public class ConvertClaimFFtoHL7 {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        BufferedWriter bufferedWriter = null;
        int i = 0;
//        String[] PtIDArray = new String[] {"0000000","1111111"};
        ArrayList<String> PtIDArray = new ArrayList<String>();
        int ii = 0;
        
        //Open the file for reading
        BufferedReader in = getReader("c:/Users/Public/Documents/BH_CLAIMS.txt");

        //Read the HL7 segments from the infile.
        CLAIMLine claimline = readCLAIM(in);
        while (claimline != null){
            //We only want to write 1 HL7 message per patient.  We are going to 
            //search an array of patient ids and if found, we don't write the 
            //file.  If it's not found, we add the patient id to the array
            //and write the file.
            boolean retval = PtIDArray.contains(claimline.PtID);
        
            if(retval){
                System.out.println("Found PtID in the ArrayList: "+claimline.PtID);
            }
            else
            {
                PtIDArray.add(claimline.PtID);
                System.out.println("Just added a record to the array: " + claimline.PtID);
                //Write a new file for ever line in the claim file.  Use a variable 
                //that is incremented to create unique filenames.
                i++;
                bufferedWriter = new BufferedWriter(new FileWriter("c:/Users/Public/HL7Files/HL7" + i + ".txt"));

                //write the hl7 segment read from the in file to the outfile.
                bufferedWriter.write(claimline.msgseg);
                // write a new line
                bufferedWriter.newLine();
                // flush
                bufferedWriter.flush();
                
            }//End If
            //read next
            claimline = readCLAIM(in);
        }//End While
//            if (found){
//            } else {
//            if (PtIDFound == 1) {
//                PtIDArray[PtIDArray.length - 1] = claimline.PtID;
    }//End Public Static Void        
    private static BufferedReader getReader(String inname)
    {
        BufferedReader in = null;
        try {
            File infile = new File(inname);
            in = new BufferedReader( new FileReader(infile));
            }
        catch (FileNotFoundException e)
        {
            System.out.println("The file doesn't exist.");
            System.exit(0);
        }
        return in;
    }
    private static CLAIMLine readCLAIM(BufferedReader in)
    {
        String msgseg;
        String line = "";
        String[] data;
        String EncStrt, EncEnd;
        String PtID, PtLN, PtFN, PtSex, PtDOB;
        String PtStreet, PtCity, PtST, PtZip;
        String CPTCde, CPTMod1, CPTMod2, Dx1, Dx2;
        String NPI, Provider, ClaimID, ClaimIDSeq, POS, DRG, RevCDE, TPID;
        
        try
        {
            line = in.readLine();
        }
        catch (IOException e)
        {
            System.out.println("I/O Error");
            System.exit(0);
        }
        if (line == null)
            return null;
        else
        {
            data = line.split("\\|");
            EncStrt = data[0];
            EncEnd  = data[1];
            PtID    = data[2];
            PtFN    = data[3];
            PtLN    = data[4];
            PtStreet    = data[5];
            PtCity      = data[6];
            PtST        = data[7];
            PtZip       = data[8];
            PtSex       = data[9];
            PtDOB       = data[10].substring(6,10) + data[10].substring(0,2) + data[10].substring(3,5);
            CPTCde      = data[11];
            CPTMod1     = data[12];
            CPTMod2     = data[13];
            Dx1         = data[14];
            Dx2         = data[15];
            NPI         = data[16];
            Provider    = data[17];
            ClaimID     = data[18];
            ClaimIDSeq  = data[19];
            POS         = data[20];
            DRG         = data[21];
            RevCDE      = data[22];
            TPID        = data[23];
            
            msgseg = "MSH|^~\\&|BH_MG_BHMG|GEMMS|BAYHLTHCDR||20150408111816||";
            msgseg += "ADT^A08|150408111816270187|P|2.3||||AL";
            msgseg += ("\r\n");
            msgseg += "ENV|A08|20140223141813||01";
            msgseg += ("\r\n");
            msgseg += "PID|1|" + PtID + "|" + PtID + "||"+PtLN+"^"+PtFN+"^||";
            msgseg += PtDOB+"|"+PtSex+"|||"+PtStreet+"^^"+PtCity+"^"+PtST+"^"+PtZip;
            msgseg += "|||||||"+PtID;
            msgseg += ("\r\n");
            msgseg += "PV1|1|0|||||||||||N|||||||Mcare A|||||||||||||||||||||||";
            msgseg += "201402090007";
            
            return new CLAIMLine(msgseg, line, PtID);
        }      
    }

//    private static void If(boolean b) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
    
    private static class CLAIMLine
    {
        public String msgseg;
        public String line;
        public String PtID;
        
        
        public CLAIMLine(String msgseg, String line, String PtID)
        {
            this.msgseg = msgseg;
            this.line = line;
            this.PtID = PtID;
            
        }
    }
}
