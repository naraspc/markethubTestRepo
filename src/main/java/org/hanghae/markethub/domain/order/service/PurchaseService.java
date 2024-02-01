package org.hanghae.markethub.domain.order.service;

import lombok.RequiredArgsConstructor;
import org.hanghae.markethub.domain.order.dto.PurchaseRequestDto;
import org.hanghae.markethub.domain.order.dto.PurchaseResponseDto;
import org.hanghae.markethub.domain.order.entity.Purchase;
import org.hanghae.markethub.domain.order.repository.PurchaseRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;

    //C
    public PurchaseResponseDto createOrder(PurchaseRequestDto purchaseRequestDto) {
        Purchase purchase = Purchase.builder()
                .status(purchaseRequestDto.status())
                .build();

        purchaseRepository.save(purchase);

        return


    }



    //R




    //U




    //D
}
