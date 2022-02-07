package com.example.deliverygo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.servlet.ModelAndView;


@Controller
@RequiredArgsConstructor
public class PortfolioQueryController {

	/*@GetMapping("/")
	public String index() {
		return "redirect:/portfolio";
	}*/

	@GetMapping("/portfolio")
	public String positions() {
		//ModelAndView model = new ModelAndView();
		//model.addObject("positionsResponse", portfolioService.getPortfolioPositions());
		//model.addObject("transaction", new AddTransactionToPortfolioDto());
		return  "portfolio";
	}

}
