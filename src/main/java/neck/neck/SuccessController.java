package neck.neck;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/*
 * Class SuccesController informs about successful finishing of specified files processing.
 * @author Martin Lisý
 */
@Controller
public class SuccessController {

	/*
	 * @param	request		HttpServletRequest from .jsp page.
	 * @return	ModelAndView object within the new .jsp page.
	 */
	@RequestMapping(value = "/success", method = RequestMethod.POST)
	public ModelAndView success(HttpServletRequest request){
		String ES = request.getParameter("ES");
		return new ModelAndView("loadFile", "ES", ES);
	}
}