package thread.control.join;

import static util.MyLogger.log;
import static util.ThreadUtils.sleep;

public class JoinMainV3 {
    public static void main(String[] args) throws InterruptedException {
        log("Start");
        log("====================");

        SumTask task1 = new SumTask(1, 50);
        SumTask task2 = new SumTask(51, 100);

        Thread thread1 = new Thread(task1, "thread-1");
        Thread thread2 = new Thread(task2, "thread-2");

        thread1.start();
        thread2.start();

        // 스레드가 종료될 때 까지 대기
        log("join() - main 스레드가 thread1, thread2 종료까지 대기");
        thread1.join();
        thread2.join();
        log("main 스레드 대기 완료");

        log("task1.result = " + task1.result);
        log("task2.result = " + task2.result);

        int sumAll = task1.result + task2.result;
        log("task1 + task2 = " + sumAll);

        log("====================");
        log("End");
    }

    static class SumTask implements Runnable {
        int startValue;
        int endValue;
        int result;

        public SumTask(int startValue, int endValue) {
            this.startValue = startValue;
            this.endValue = endValue;
        }

        /**
         * this 는 호출된 인스턴스 메서드가 소속된 객체를 가리키는 참조이며, 이것이 스택 프레임 내부에 저장
         */
        @Override
        public void run() {
            log("작업 시작");

            sleep(2000);
            int sum = 0;
            for (int i = this.startValue; i <= this.endValue; i++) {
                sum += i;
            }
            this.result = sum;

            log("작업 완료 result = " + this.result);
        }
    }
}
