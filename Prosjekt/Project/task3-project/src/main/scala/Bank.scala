import java.util.NoSuchElementException

import akka.actor._
import java.util.concurrent.atomic.AtomicInteger
import scala.concurrent.duration._
import akka.util.Timeout

case class GetAccountRequest(accountId: String)

case class CreateAccountRequest(initialBalance: Double)

case class IdentifyActor()

class Bank(val bankId: String) extends Actor {

  val accountCounter = new AtomicInteger(1000)

  def createAccount(initialBalance: Double): ActorRef = {
    // Should create a new Account Actor and return its actor reference. Accounts should be assigned with unique ids (increment with 1).
    val newId: Int = accountCounter.incrementAndGet()
    BankManager.createAccount(newId.toString, bankId, initialBalance)
  }

  def findAccount(accountId: String): Option[ActorRef] = {
    // Use BankManager to look up an account with ID accountId
    Some(BankManager.findAccount(bankId,accountId))
  }

  def findOtherBank(bankId: String): Option[ActorRef] = {
    // Use BankManager to look up a different bank with ID bankId
    Some(BankManager.findBank(bankId))
  }

  override def receive = {
    case CreateAccountRequest(initialBalance) => sender ! createAccount(initialBalance) // Create a new account
    case GetAccountRequest(id) => sender ! findAccount(id) // Return account
    case IdentifyActor => sender ! this
    case t: Transaction => processTransaction(t)

    case t: TransactionRequestReceipt => {
      // Forward receipt
      val konto = t.toAccountNumber
      var internal = konto.length <= 4
      val tilBank = if(internal) bankId else konto.substring(0,4)
      val tilKonto = if(internal) konto else konto.substring(4)
      if (tilBank == bankId){
        internal = true
      }

      if(internal){
        var mottaKonto: Option[ActorRef] = null
        try{
          mottaKonto = findAccount(tilKonto)
          if(mottaKonto.isEmpty){
            t.transaction.status = TransactionStatus.FAILED
          }else{
            mottaKonto.get ! t
          }
        }catch{
          case exc: NoSuchElementException =>
            t.transaction.status = TransactionStatus.FAILED
        }
      }else{
        var ExternalBank = findOtherBank(tilBank)
        if(ExternalBank.isEmpty){
          t.transaction.status = TransactionStatus.FAILED
          this.self ! t
        }else{
          ExternalBank.get ! t
        }
      }
    }

    case msg => ???
  }

  def processTransaction(t: Transaction): Unit = {
    implicit val timeout = new Timeout(5 seconds)
    var isInternal = t.to.length <= 4
    val toBankId = if (isInternal) bankId else t.to.substring(0, 4)
    val toAccountId = if (isInternal) t.to else t.to.substring(4)
    val transactionStatus = t.status
    
    // This method should forward Transaction t to an account or another bank, depending on the "to"-address.
    // HINT: Make use of the variables that have been defined above.
    if(toBankId == bankId){
      isInternal = true
    }

    if(isInternal){
      var mottaKonto: Option[ActorRef] = null

      try{
        mottaKonto = findAccount(toAccountId)
        if(mottaKonto.isEmpty){
          t.status = TransactionStatus.FAILED
          sender ! new TransactionRequestReceipt(t.to, t.id, t)
        }else{
          mottaKonto.get ! t
        }
      }catch{
        case exc: NoSuchElementException =>
          t.status = TransactionStatus.FAILED
          sender ! new TransactionRequestReceipt(t.to, t.id, t)
      }
    }else{
      try{
        val external = findOtherBank(toBankId)
        if(external.isEmpty){
          t.status = TransactionStatus.FAILED
          sender ! new TransactionRequestReceipt(t.to, t.id, t)
        }else{
          external.get ! t
        }
      }catch{
        case exc: NoSuchElementException =>
          t.status = TransactionStatus.FAILED
          sender ! new TransactionRequestReceipt(t.to, t.id, t)
      }
    }
  }
}