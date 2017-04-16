import java.util.concurrent.atomic.AtomicInteger

import exceptions.{IllegalAmountException, NoSufficientFundsException}

object Bank {

  private val idCounter = new AtomicInteger(0)

  def transaction(from: Account, to: Account, amount: Double): Unit = {
    if(from.getBalanceAmount < amount){
      throw new NoSufficientFundsException("For stort beløp!")
    }else if(amount < 0){
      throw new IllegalAmountException("Beløp må være større enn null")
    }else{
      from.withdraw(amount)
      to.deposit(amount)
    }
  }

  def getUniqueId: Int = {
    idCounter.getAndIncrement()
  }
}
