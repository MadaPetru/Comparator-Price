package ro.adi.comparatorprices.product.sites;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SiteUtility {

    public int getPriceFromHtmlPageResultSearchingAfterApparitionStringFromTheEndOfThePriceWith2CharactersDistance(
            String linkResult, String searchAfterApparitionPrice) {
        var indexToBeSubtractToGetOverTheLettersAndStartToExtractThePriceInDigits = 2;
        var indexOfApparitionOfPrice = linkResult.lastIndexOf(searchAfterApparitionPrice)
                - indexToBeSubtractToGetOverTheLettersAndStartToExtractThePriceInDigits;
        var multiplicationForDecimalsToInvertThePriceBecauseWeLookForPriceFromRightToLeft = 1;
        var price = 0;
        var character = linkResult.charAt(indexOfApparitionOfPrice);
        while (isCharacterDigit(character)) {
            price = price
                    + multiplicationForDecimalsToInvertThePriceBecauseWeLookForPriceFromRightToLeft * (character - '0');
            multiplicationForDecimalsToInvertThePriceBecauseWeLookForPriceFromRightToLeft *= 10;
            indexOfApparitionOfPrice--;
            character = linkResult.charAt(indexOfApparitionOfPrice);
        }
        return price;
    }

    private boolean isCharacterDigit(char character) {
        return character >= '0' && character <= '9';
    }
}
