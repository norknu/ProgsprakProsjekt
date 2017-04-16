object Main extends App {

  def thread(body: => Unit): Thread = {
      val t = new Thread {
        override def run() = body
      }
      t.start
      t
    }

    val acc = new Account(50000)
    val first = Main.thread {
      for (i<- 0 until 100) { acc.withdraw(10); Thread.sleep(10) }
    }
    val second = Main.thread {
      for (i<- 0 until 100) { acc.deposit(5); Thread.sleep(20) }
    }
    val third = Main.thread {
      for (i<- 0 until 100) { acc.withdraw(50); Thread.sleep(10) }
    }
    val fourth = Main.thread {
      for (i<- 0 until 100) { acc.deposit(100); Thread.sleep(10) }
    }
    first.join(); second.join(); third.join(); fourth.join()
    assert (acc.getBalanceAmount == 54500)

  // Write a few transaction examples using Threads
  print(acc.getBalanceAmount)
}
