import exceptions._
import scala.collection.mutable
import scala.collection.mutable.{Queue}

object TransactionStatus extends Enumeration {
  val SUCCESS, PENDING, FAILED = Value
}

class TransactionQueue {
  private val locq: Queue[Transaction] = new Queue


  // Remove and return the first element from the queue
  def pop: Transaction = {
    locq.synchronized{
      locq.dequeue()
    }
  }

  // Return whether the queue is empty
  def isEmpty: Boolean = {
    locq.synchronized{
      locq.isEmpty
    }
  }

  // Add new element to the back of the queue
  def push(t: Transaction): Unit = {
    locq.synchronized{
      locq.enqueue(t)
    }
  }

  // Return the first element from the queue without removing it
  def peek: Transaction ={
    locq.synchronized{
      locq.front //eventuelt locq.head
    }
  }

  // Return an iterator to allow you to iterate over the queue
  def iterator: Iterator[Transaction] = {
    locq.synchronized{
      locq.iterator
    }
  }
}

class Transaction(val transactionsQueue: TransactionQueue,
                  val processedTransactions: TransactionQueue,
                  val from: Account,
                  val to: Account,
                  val amount: Double,
                  val allowedAttemps: Int) extends Runnable {

  var status: TransactionStatus.Value = TransactionStatus.PENDING
  var forsok = 0

  override def run: Unit = {

    def doTransaction() = {
      println("HALLOOOO overfører cashmoneyflow")

      try{
        from withdraw amount
        to deposit amount
        status = TransactionStatus.SUCCESS
        processedTransactions.push(this)
      }catch{
        case exc: NoSufficientFundsException =>
          print(forsok)
          forsok +=1
          if(forsok == allowedAttemps){
            status = TransactionStatus.FAILED
            processedTransactions.push(this)
          }else{
            transactionsQueue.push(this)
          }
        case exc: IllegalAmountException =>
          println("BROO")
          status = TransactionStatus.FAILED
          processedTransactions.push(this)
      }


    }

    if (from.uid < to.uid) from synchronized {
      to synchronized {
        doTransaction
      }
    } else to synchronized {
      from synchronized {
        doTransaction
      }
    }

    // Extend this method to satisfy new requirements.

  }
}
