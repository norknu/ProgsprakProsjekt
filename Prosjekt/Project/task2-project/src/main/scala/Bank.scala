import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

import scala.concurrent.forkjoin.ForkJoinPool

class Bank(val allowedAttempts: Integer = 3) {

  private val uid = new AtomicInteger(0)
  private val transactionsQueue: TransactionQueue = new TransactionQueue()
  private val processedTransactions: TransactionQueue = new TransactionQueue()
  private val executorContext = Executors newFixedThreadPool(16) //16 tråder som kan kjøre om gangen

  def addTransactionToQueue(from: Account, to: Account, amount: Double): Unit = {
    transactionsQueue push new Transaction(
      transactionsQueue, processedTransactions, from, to, amount, allowedAttempts)

      this.synchronized(processTransactions)
      executorContext.submit(new Runnable {
        override def run(): Unit = {
          processTransactions
        }
      })
  }

  def generateAccountId: Int = {
    uid.getAndIncrement()
  }

  private def processTransactions: Unit = {

    while (!transactionsQueue.isEmpty) {
      val ko = transactionsQueue.pop
      executorContext.submit(ko)
    }
    Thread.sleep(250)
    executorContext.submit(new Runnable {
      override def run(): Unit = {
        processTransactions
      }
    })
  }

  def addAccount(initialBalance: Double): Account = {
    new Account(this, initialBalance)
  }

  def getProcessedTransactionsAsList: List[Transaction] = {
    processedTransactions.iterator.toList
  }

}
