
import java.util.ArrayList;
import java.util.List;
import com.nudge.apm.buffer.probe.RawDataProtocol.RawData;

public class MbeanTest {

	public static void main(String[] args) {

		System.out.println("test : ");
	//	List<RawData> rawdata = test(rawdata);
		
	}

	public static List<String> test(List<RawData> rawdata) {
		
		

		List<String> addDico = new ArrayList<String>();
		
		for (RawData rd : rawdata) {
			rd.getMbeanDictionary().getDictionary(0);
			System.out.println(rd);
			System.out.println(rd);
		}
		return addDico;



	}

}
