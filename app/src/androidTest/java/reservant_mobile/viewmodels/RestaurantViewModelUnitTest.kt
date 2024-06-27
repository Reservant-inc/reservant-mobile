package reservant_mobile.viewmodels

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Test
import reservant_mobile.ApplicationService
import reservant_mobile.ui.viewmodels.RestaurantViewModel

class RestaurantViewModelUnitTest {

    @Test
    fun no_error_on_correct_first_step() = runTest {
        val vm = RestaurantViewModel()
        vm.name.value = "JD restaurant"
        vm.restaurantType.value = "Restaurant"
        vm.nip.value = "1060000062"
        vm.address.value = "Koszykowa 86"
        vm.postalCode.value = "02-008"
        vm.city.value = "Warszawa"
        val result = vm.isRestaurantRegistrationFirstStepInvalid()
        assertThat(result).isFalse()
    }

    @Test
    fun no_error_when_name_is_correct(){
        val vm = RestaurantViewModel()
        vm.name.value = "JD Restaurant"
        assertThat(vm.isNameInvalid()).isFalse()
    }
    @Test
    fun error_when_name_is_empty(){
        val vm = RestaurantViewModel()
        vm.name.value = ""
        assertThat(vm.isNameInvalid()).isTrue()
        vm.name.value = " "
        assertThat(vm.isNameInvalid()).isTrue()
    }

    @Test
    fun error_when_name_is_invalid(){
        val vm = RestaurantViewModel()
        vm.name.value = "!@#$%^&*()_+"
        assertThat(vm.isNameInvalid()).isTrue()
    }

    @Test
    fun no_error_when_type_is_correct(){
        val vm = RestaurantViewModel()
        vm.restaurantType.value = "Restaurant"
        assertThat(vm.isRestaurantTypeInvalid()).isFalse()
    }
    @Test
    fun error_when_type_is_empty(){
        val vm = RestaurantViewModel()
        vm.restaurantType.value = ""
        assertThat(vm.isRestaurantTypeInvalid()).isTrue()
        vm.restaurantType.value = " "
        assertThat(vm.isRestaurantTypeInvalid()).isTrue()
    }

    @Test
    fun error_when_type_is_invalid(){
        val vm = RestaurantViewModel()
        vm.restaurantType.value = "!@#$%^&*()_+"
        assertThat(vm.isRestaurantTypeInvalid()).isTrue()
    }

    @Test
    fun no_error_when_nip_is_correct(){
        val vm = RestaurantViewModel()
        vm.nip.value = "1060000062"
        assertThat(vm.isNipInvalid()).isFalse()
    }
    @Test
    fun error_when_nip_is_empty(){
        val vm = RestaurantViewModel()
        vm.nip.value = ""
        assertThat(vm.isNipInvalid()).isTrue()
        vm.nip.value = " "
        assertThat(vm.isNipInvalid()).isTrue()
    }

    @Test
    fun error_when_nip_is_invalid(){
        val vm = RestaurantViewModel()
        vm.nip.value = "1060000000"
        assertThat(vm.isNipInvalid()).isTrue()
    }

    @Test
    fun no_error_when_address_is_correct(){
        val vm = RestaurantViewModel()
        vm.address.value = "Koszykowa 86"
        assertThat(vm.isAddressInvalid()).isFalse()
    }
    @Test
    fun error_when_address_is_empty(){
        val vm = RestaurantViewModel()
        vm.name.value = ""
        assertThat(vm.isAddressInvalid()).isTrue()
        vm.name.value = " "
        assertThat(vm.isAddressInvalid()).isTrue()
    }

    @Test
    fun no_error_when_postalCode_is_correct(){
        val vm = RestaurantViewModel()
        vm.postalCode.value = "02-008"
        assertThat(vm.isPostalCodeInvalid()).isFalse()
    }
    @Test
    fun error_when_postalCode_is_empty(){
        val vm = RestaurantViewModel()
        vm.postalCode.value = ""
        assertThat(vm.isPostalCodeInvalid()).isTrue()
        vm.postalCode.value = " "
        assertThat(vm.isPostalCodeInvalid()).isTrue()
    }

    @Test
    fun error_when_postalCode_is_invalid(){
        val vm = RestaurantViewModel()
        vm.postalCode.value = "02008"
        assertThat(vm.isPostalCodeInvalid()).isTrue()
    }

    @Test
    fun no_error_when_city_is_correct(){
        val vm = RestaurantViewModel()
        vm.city.value = "Warsaw"
        assertThat(vm.isCityInvalid()).isFalse()
    }
    @Test
    fun error_when_city_is_empty(){
        val vm = RestaurantViewModel()
        vm.city.value = ""
        assertThat(vm.isCityInvalid()).isTrue()
        vm.city.value = " "
        assertThat(vm.isCityInvalid()).isTrue()
    }

    @Test
    fun no_error_when_description_is_correct(){
        val vm = RestaurantViewModel()
        vm.description.value = "Test description"
        assertThat(vm.isDescriptionInvalid()).isFalse()
    }
    @Test
    fun error_when_description_is_empty(){
        val vm = RestaurantViewModel()
        vm.description.value = ""
        assertThat(vm.isDescriptionInvalid()).isTrue()
        vm.description.value = " "
        assertThat(vm.isDescriptionInvalid()).isTrue()
    }


    @Test
    fun error_when_rentalContract_is_empty(){
        val vm = RestaurantViewModel()
        vm.rentalContract.value = ""
        assertThat(vm.isRentalContractInvalid(ApplicationService.instance)).isTrue()
        vm.rentalContract.value = " "
        assertThat(vm.isRentalContractInvalid(ApplicationService.instance)).isTrue()
    }

    @Test
    fun error_when_rentalContract_is_invalid(){
        val vm = RestaurantViewModel()
        vm.rentalContract.value = "example.png"
        assertThat(vm.isRentalContractInvalid(ApplicationService.instance)).isTrue()
    }

    @Test
    fun error_when_alcoholLicense_is_empty(){
        val vm = RestaurantViewModel()
        vm.alcoholLicense.value = ""
        assertThat(vm.isAlcoholLicenseInvalid(ApplicationService.instance)).isTrue()
        vm.alcoholLicense.value = " "
        assertThat(vm.isRentalContractInvalid(ApplicationService.instance)).isTrue()
    }

    @Test
    fun error_when_alcoholLicense_is_invalid(){
        val vm = RestaurantViewModel()
        vm.alcoholLicense.value = "example.png"
        assertThat(vm.isAlcoholLicenseInvalid(ApplicationService.instance)).isTrue()
    }

    @Test
    fun error_when_businessPermission_is_empty(){
        val vm = RestaurantViewModel()
        vm.businessPermission.value = ""
        assertThat(vm.isBusinessPermissionInvalid(ApplicationService.instance)).isTrue()
        vm.businessPermission.value = " "
        assertThat(vm.isBusinessPermissionInvalid(ApplicationService.instance)).isTrue()
    }

    @Test
    fun error_when_businessPermission_is_invalid(){
        val vm = RestaurantViewModel()
        vm.businessPermission.value = "example.png"
        assertThat(vm.isBusinessPermissionInvalid(ApplicationService.instance)).isTrue()
    }

    @Test
    fun error_when_idCard_is_empty(){
        val vm = RestaurantViewModel()
        vm.idCard.value = ""
        assertThat(vm.isIdCardInvalid(ApplicationService.instance)).isTrue()
        vm.idCard.value = " "
        assertThat(vm.isIdCardInvalid(ApplicationService.instance)).isTrue()
    }

    @Test
    fun error_when_idCard_is_invalid(){
        val vm = RestaurantViewModel()
        vm.idCard.value = "example.png"
        assertThat(vm.isIdCardInvalid(ApplicationService.instance)).isTrue()
    }

    @Test
    fun error_when_logo_is_empty(){
        val vm = RestaurantViewModel()
        vm.logo.value = ""
        assertThat(vm.isLogoInvalid(ApplicationService.instance)).isTrue()
        vm.logo.value = " "
        assertThat(vm.isLogoInvalid(ApplicationService.instance)).isTrue()
    }

    @Test
    fun error_when_logo_is_invalid(){
        val vm = RestaurantViewModel()
        vm.logo.value = "example.pdf"
        assertThat(vm.isLogoInvalid(ApplicationService.instance)).isTrue()
    }
}