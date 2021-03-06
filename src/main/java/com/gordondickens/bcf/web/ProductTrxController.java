package com.gordondickens.bcf.web;

import com.gordondickens.bcf.entity.Product;
import com.gordondickens.bcf.entity.ProductTrx;
import com.gordondickens.bcf.repository.ProductRepository;
import com.gordondickens.bcf.repository.ProductTrxRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Controller
@RequestMapping("/producttrxes")
public class ProductTrxController {

    @Inject
    ProductTrxRepository repository;

    @Inject
    ProductRepository productRepository;

    @RequestMapping(method = RequestMethod.POST)
    public String create(@Valid ProductTrx productTrx,
                         BindingResult bindingResult, Model uiModel,
                         HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("productTrx", productTrx);
            return "producttrxes/create";
        }
        uiModel.asMap().clear();
        repository.save(productTrx);
        return "redirect:/producttrxes/"
                + encodeUrlPathSegment(productTrx.getId().toString(),
                httpServletRequest);
    }

    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String createForm(Model uiModel) {
        uiModel.addAttribute("productTrx", new ProductTrx());
        List<String[]> dependencies = new ArrayList<String[]>();
        if (repository.count() == 0) {
            dependencies.add(new String[]{"product", "products"});
        }
        uiModel.addAttribute("dependencies", dependencies);
        return "producttrxes/create";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String show(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("producttrx", repository.findOne(id));
        uiModel.addAttribute("itemId", id);
        return "producttrxes/show";
    }

    @RequestMapping(method = RequestMethod.GET)
    public String list(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            uiModel.addAttribute("producttrxes",
                    repository.findAll());
            // TODO: add pagination
            // .findProductTrxEntries(page == null ? 0
            // : (page.intValue() - 1) * sizeNo, sizeNo));
            float nrOfPages = (float) repository.count() / sizeNo;
            uiModel.addAttribute(
                    "maxPages",
                    (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1
                            : nrOfPages));
        } else {
            uiModel.addAttribute("producttrxes",
                    repository.findAll());
        }
        return "producttrxes/list";
    }

    @RequestMapping(method = RequestMethod.PUT)
    public String update(@Valid ProductTrx productTrx,
                         BindingResult bindingResult, Model uiModel,
                         HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("productTrx", productTrx);
            return "producttrxes/update";
        }
        uiModel.asMap().clear();
        repository.save(productTrx);
        return "redirect:/producttrxes/"
                + encodeUrlPathSegment(productTrx.getId().toString(),
                httpServletRequest);
    }

    @RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("productTrx", repository.findOne(id));
        return "producttrxes/update";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String delete(@PathVariable("id") Long id,
                         @RequestParam(value = "page", required = false) Integer page,
                         @RequestParam(value = "size", required = false) Integer size,
                         Model uiModel) {
        repository.delete(id);
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/producttrxes";
    }

    @ModelAttribute("product")
    public Collection<Product> populateProducts() {
        return productRepository.findAll();
    }

    @ModelAttribute("producttrxes")
    public Collection<ProductTrx> populateProductTrxes() {
        return repository.findAll();

    }

    String encodeUrlPathSegment(String pathSegment,
                                HttpServletRequest httpServletRequest) {
        String enc = httpServletRequest.getCharacterEncoding();
        if (enc == null) {
            enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        } catch (UnsupportedEncodingException ignored) {
        }
        return pathSegment;
    }
}
