//package org.hanghae.markethub.domain.purchase.dto;
//
//import org.hanghae.markethub.domain.cart.entity.Cart;
//import org.hanghae.markethub.domain.item.entity.Item;
//import org.hanghae.markethub.domain.purchase.entity.Purchase;
//import org.hanghae.markethub.global.constant.Status;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.stream.Collectors;
//
//public record PurchaseResponseDto(
//        Long purchaseId,
//        Status status,
//        List<CartDetailsDto> carts,
//        ItemDetailsDto itemDetails
//) {
//    public static PurchaseResponseDto fromPurchase(Purchase purchase) {
//        List<CartDetailsDto> cartDetailsDto = purchase.getCart() != null ? purchase.getCart().stream()
//                .map(cart -> new CartDetailsDto(
//                        cart.getCartId(),
//                        cart.getPrice(),
//                        cart.getQuantity(),
//                        cart.getAddress()))
//                .toList() : Collections.emptyList();
//
//        ItemDetailsDto itemDetails = createItemDetailsDto(purchase.getItem());
//
//        return new PurchaseResponseDto(
//                purchase.getId(),
//                purchase.getStatus(),
//                cartDetailsDto,
//                itemDetails
//        );
//    }
//
//    public static PurchaseResponseDto fromSingleItemPurchase(Purchase purchase) {
//        // 동일한 아이템 세부 정보 생성 로직을 사용
//        ItemDetailsDto itemDetails = createItemDetailsDto(purchase.getItem());
//
//        return new PurchaseResponseDto(
//                purchase.getId(),
//                purchase.getStatus(),
//                Collections.emptyList(), // 단일 아이템 구매에서는 carts 리스트는 비어있음
//                itemDetails
//        );
//    }
//
//    private static ItemDetailsDto createItemDetailsDto(Item item) {
//        if (item != null) {
//            return new ItemDetailsDto(
//                    item.getId(),
//                    item.getItemName(),
//                    item.getPrice(),
//                    item.getQuantity(),
//                    item.getItemInfo(),
//                    item.getCategory()
//            );
//        }
//        return null;
//    }
//
//    public static List<PurchaseResponseDto> fromListPurchaseEntity(List<Purchase> purchases) {
//        return purchases.stream()
//                .map(
//                        PurchaseResponseDto
//                                ::fromPurchase)
//                .collect(Collectors.toList());
//    }
//
//    public record CartDetailsDto(
//            Long cartId,
//            int quantity,
//            int price,
//            String address
//    ) {
//    }
//
//    public record ItemDetailsDto(
//            Long itemId,
//            String itemName,
//            int price,
//            int quantity,
//            String itemInfo,
//            String category
//    ) {
//    }
//}