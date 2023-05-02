package com.ahmed.miniprojet1.controllers;

import com.ahmed.miniprojet1.entities.Bus;
import com.ahmed.miniprojet1.entities.Marque;
import com.ahmed.miniprojet1.service.BusService;
import com.ahmed.miniprojet1.service.MarqueService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
public class BusController {

        @Autowired
        BusService busService;

        @Autowired
        MarqueService marqueService;

        @RequestMapping("/showCreate")
        public String showCreate(ModelMap modelMap){
            List<Marque> marque = marqueService.getAllMarque();
            modelMap.addAttribute("bus", new Bus());
            modelMap.addAttribute("mode", "new");
            modelMap.addAttribute("marque", marque);
            modelMap.addAttribute("page",0);
            return "formBus";
        }

    @RequestMapping("/saveBus")
    public String saveProduit(@Valid Bus bus,
                              BindingResult bindingResult,
                              @ModelAttribute("page") int pageFromPrevious,
                              @RequestParam (name="size", defaultValue = "5") int size,
                              RedirectAttributes redirectAttributes)
    {
        int page;
        if (bindingResult.hasErrors()) return "formBus";

        if (bus.getIdBus()==null) //adding
            page = busService.getAllBus().size()/size; // calculer le nbr de pages
        else
            page = pageFromPrevious;

        busService.saveBus(bus);

        redirectAttributes.addAttribute("page", page);
        return "redirect:/ListeBus";
    }


    @RequestMapping("/ListeBus")
        public String listeBuss(ModelMap modelMap,
                                    @RequestParam (name="page",defaultValue = "0") int page,
                                    @RequestParam (name="size", defaultValue = "5") int size)
        {
            Page<Bus> bus = busService.getAllBusParPage(page, size);
            modelMap.addAttribute("buss", bus);
            modelMap.addAttribute("pages", new int[bus.getTotalPages()]);
            modelMap.addAttribute("currentPage", page);
            modelMap.addAttribute("size", size);
            return "listeBus";
        }

        @RequestMapping("/supprimerBus")
        public String supprimerBus(@RequestParam("id") Long id,
                                       ModelMap modelMap,
                                       @RequestParam (name="page",defaultValue = "0") int page,
                                       @RequestParam (name="size", defaultValue = "5") int size)
        {
            busService.deleteBusById(id);
            Page<Bus> bus = busService.getAllBusParPage(page, size);
            modelMap.addAttribute("buss", bus);
            modelMap.addAttribute("pages", new int[bus.getTotalPages()]);
            modelMap.addAttribute("currentPage", page);
            modelMap.addAttribute("size", size);
            return "listeBus";
        }


        @RequestMapping("/modifierBus")
        public String editerBus(@RequestParam("id") Long id,
                                @RequestParam("page") int page,
                                ModelMap modelMap
        )
        {
            Bus bus= busService.getBus(id);
            List<Marque> marque = marqueService.getAllMarque();
            modelMap.addAttribute("bus", bus);
            modelMap.addAttribute("marque", marque);
            modelMap.addAttribute("selectedType", bus.getMarque());
            modelMap.addAttribute("mode", "edit");
            modelMap.addAttribute("page",page);
            return "formBus";
        }

        @RequestMapping("/updateBus")
        public String updateProduit(@ModelAttribute("bus") Bus bus, @RequestParam("date") String date, ModelMap modelMap) throws ParseException {
            //conversion de la date
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
            Date dateMise = dateformat.parse(String.valueOf(date));
            bus.setDateMisenService(dateMise);

            busService.updateBus(bus);
            return "redirect:/ListeBus";
        }
}
