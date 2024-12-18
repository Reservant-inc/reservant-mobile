import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import reservant_mobile.data.models.dtos.MoneyDTO
import reservant_mobile.data.services.UserService
import reservant_mobile.ui.viewmodels.ReservantViewModel

class WalletViewModel(
    private val userService: UserService = UserService()
): ReservantViewModel() {

    var balance = mutableStateOf<Double?>(null)
        private set

    var errorMessage = mutableStateOf<String?>(null)
        private set

    private var _walletHistoryFlow: Flow<PagingData<MoneyDTO>>? = null
    val walletHistoryFlow: Flow<PagingData<MoneyDTO>>?
        get() = _walletHistoryFlow

    init {
        getWalletBalance()
        viewModelScope.launch {
            val result = userService.getWalletHistory()
            if (result.isError || result.value == null) {
                errorMessage.value = "Failed to load history"
            } else {
                _walletHistoryFlow = result.value!!.cachedIn(viewModelScope)
            }
        }
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
                onSuccess()
            }
        }
    }
}
