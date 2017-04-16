import exceptions._

class Account(val bank: Bank, initialBalance: Double) {

  class Balance(var amount: Double) {}

  val balance = new Balance(initialBalance)
  val uid = bank.generateAccountId

  def withdraw(amount: Double): Unit = {
    if (amount<0){
      throw new IllegalAmountException("Må være et beløp større enn 0!")
    }else if (amount>balance.amount){
      throw new NoSufficientFundsException("Beløpet for stort!")
    }else{
      this.synchronized(balance.amount -= amount)
    }
  }
  def deposit(amount: Double): Unit = {
    if(amount<0){
      throw new IllegalAmountException("Må overføre et beløp på mer enn 0 kr")
    }else{
      this.synchronized(balance.amount += amount)
    }
  }

  def getBalanceAmount: Double = {
    return balance.amount
  }

  def transferTo(account: Account, amount: Double) = {
    bank addTransactionToQueue (this, account, amount)
  }


}
