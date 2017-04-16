import exceptions.{NoSufficientFundsException, IllegalAmountException}

class Account(initialBalance: Double, val uid: Int = Bank getUniqueId) {

  var tall: Double = initialBalance

  def withdraw(amount: Double): Unit = {
    if (amount<=0){
      throw new IllegalAmountException("Må være et beløp større enn 0!")
    }
    if (amount>initialBalance){
      throw new NoSufficientFundsException("Beløpet for stort!")
    }else{
      this.synchronized(tall -= amount)
    }
  }
  def deposit(amount: Double): Unit = {
    if(amount<=0){
      throw new IllegalAmountException("Må overføre et beløp på mer enn 0 kr")
    }else{
      this.synchronized(tall+=amount)
    }
  }
  def getBalanceAmount: Double = {
    return tall
  }
}
