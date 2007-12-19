package se.citerus.dddsample.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import se.citerus.dddsample.domain.Cargo;
import se.citerus.dddsample.service.CargoService;

public class DispatchController extends MultiActionController {
  private CargoService cargoService;
  
  public ModelAndView cargo(HttpServletRequest request, HttpServletResponse response) throws Exception {
    Cargo cargo = cargoService.find("XYZ");
    
    ModelAndView mav = new ModelAndView("cargo");
    mav.addObject("location", cargo.currentLocation());
    
    return mav;
  }
  

  public CargoService getCargoService() {
    return cargoService;
  }

  public void setCargoService(CargoService cargoService) {
    this.cargoService = cargoService;
  }
}
