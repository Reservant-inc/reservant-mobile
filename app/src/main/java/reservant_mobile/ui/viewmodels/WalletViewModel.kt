import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import reservant_mobile.data.models.dtos.MoneyDTO
import reservant_mobile.data.services.UserService

class WalletViewModel(
    private val userService: UserService = UserService()
) : ViewModel() {

    var balance = mutableStateOf<Double?>(null)
        private set

    var errorMessage = mutableStateOf<String?>(null)
        private set

    var walletHistoryFlow = mutableStateOf<Flow<PagingData<MoneyDTO>>?>(null)
        private set

    init {
        getWalletBalance()
        refreshWalletHistory()
    }

    fun getWalletBalance() {
        viewModelScope.launch {
            val res = userService.getWalletBalance()
            if (res.isError) {
                errorMessage.value = "Failed to load balance"
            } else {
                balance.value = res.value
            }
        }
    }

    fun refreshWalletHistory() {
        viewModelScope.launch {
            val result = userService.getWalletHistory()
            if (result.isError || result.value == null) {
                errorMessage.value = "Failed to load history"
            } else {
                      walletHistoryFlow.value = result.value!!.cachedIn(viewModelScope)
            }
        }
    }

    fun addMoneyToWallet(amount: Double, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val moneyDTO = MoneyDTO(
                title = "Added Funds",
                amount = amount
            )
            val res = userService.addMoneyToWallet(moneyDTO)
            if (res.isError || res.value == false) {
                errorMessage.value = "Failed to add money"
            } else {
                getWalletBalance()
                refreshWalletHistory()
                onSuccess()
            }
        }
    }
}
