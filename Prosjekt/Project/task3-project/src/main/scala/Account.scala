import akka.actor._
import exceptions._
import scala.collection.immutable.HashMap

case class TransactionRequest(toAccountNumber: String, amount: Double)

case class TransactionRequestReceipt(toAccountNumber: String,
                                     transactionId: String,
                                     transaction: Transaction)

case class BalanceRequest()

class Account(val accountId: String, val bankId: String, val initialBalance: Double = 0) extends Actor {

  private var transactions = HashMap[String, Transaction]()

  class Balance(var amount: Double) {}

  val balance = new Balance(initialBalance)

  def getFullAddress: String = {
    bankId + accountId
  }

  def getTransactions: List[Transaction] = {
    // Should return a list of all Transaction-objects stored in transactions
    transactions.values.toList
  }

  def allTransactionsCompleted: Boolean = {
    // Should return whether all Transaction-objects in transactions are completed
    for(a <- transactions.values){
      if(!a.isCompleted ){
        return false
      }
    }
    true
  }

  def withdraw(amount: Double): Unit = {
    if (amount < 0){
      throw new IllegalAmountException("Må være et beløp større enn 0!")
    }else if(amount > balance.amount){
      throw new NoSufficientFundsException("Beløpet for stort!")
    }else{
      this.synchronized(balance.amount-=amount)
    }
  }

  def deposit(amount: Double): Unit = {
    if(amount < 0){
      throw new IllegalAmountException()
    } else{
      this.synchronized(balance.amount += amount)
    }
  } // Like in part 1

  def getBalanceAmount: Double = {
    return balance.amount
  } // Like in part 1

  def sendTransactionToBank(t: Transaction): Unit = {
    // Should send a message containing t to the bank of this account
    var bank: ActorRef = BankManager.findBank(bankId)
    bank ! t
  }

  def transferTo(accountNumber: String, amount: Double): Transaction = {

    val t = new Transaction(from = getFullAddress, to = accountNumber, amount = amount)

    if (reserveTransaction(t)) {
      try {
        withdraw(amount)
        sendTransactionToBank(t)

      } catch {
        case _: NoSufficientFundsException | _: IllegalAmountException =>
          t.status = TransactionStatus.FAILED
      }
    }

    t

  }

  def reserveTransaction(t: Transaction): Boolean = {
    if (!transactions.contains(t.id)) {
      transactions += (t.id -> t)
      return true
    }
    false
  }

  override def receive = {
    case IdentifyActor => sender ! this

    case TransactionRequestReceipt(to, transactionId, transaction) => {
      // Process receipt
      if(transactions.contains(transactionId)){
        var transac = transactions.get(transactionId)
        var trans = transac.get
        trans.receiptReceived = true
        trans.status = transaction.status

        if(!transaction.isSuccessful){
          this.deposit(transaction.amount)
        }
      }
    }

    case BalanceRequest => sender ! getBalanceAmount // Should return current balance

    case t: Transaction => {
      // Handle incoming transaction
      try{
        this.deposit(t.amount)
        t.status = TransactionStatus.SUCCESS
      }catch{
        case exc: IllegalAmountException =>
          t.status = TransactionStatus.FAILED
      }
      sender ! new TransactionRequestReceipt(t.from, t.id, t)
    }

    case msg => ???
  }


}
