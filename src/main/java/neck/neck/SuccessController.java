package neck.neck;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class SuccessController {

	@RequestMapping(value = "/success", method = RequestMethod.POST)
	public String success(){
		return "loadFile";
	}
}