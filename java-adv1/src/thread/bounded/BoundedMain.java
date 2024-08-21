package thread.bounded;

import java.util.ArrayList;
import java.util.List;

import static util.MyLogger.log;
import static util.ThreadUtils.sleep;

public class BoundedMain {

    public static void main(String[] args) {
        // 1. BoundedQueue 선택
        BoundedQueue queue = new BoundedQueueV2(2);

        // 2. 생산자, 소비자 실행 순서 선택, 반드시 하나만 선택!
        /**
         * == [생산자 먼저 실행] 시작, BoundedQueueV2 ==
         *
         * 생산자 시작
         * [생산 시도] data1 -> []
         * [생산 완료] data1 -> [data1]
         * [생산 시도] data2 -> [data1]
         * [생산 완료] data2 -> [data1, data2]
         * [생산 시도] data3 -> [data1, data2]
         * [put] 큐가 가득 참, 생산자 대기
         *
         * 현재 상태 출력, 큐 데이터: [data1, data2]
         * producer1: TERMINATED
         * producer2: TERMINATED
         * producer3: TIMED_WAITING
         *
         * 소비자 시작
         * [소비 시도] ? <- [data1, data2]
         * [소비 시도] ? <- [data1, data2]
         * [소비 시도] ? <- [data1, data2]
         *
         * 현재 상태 출력, 큐 데이터: [data1, data2]
         * producer1: TERMINATED
         * producer2: TERMINATED
         * producer3: TIMED_WAITING
         * consumer1: BLOCKED
         * consumer2: BLOCKED
         * consumer3: BLOCKED
         *
         * == [생산자 먼저 실행] 종료, BoundedQueueV2 ==
         *
         * [put] 큐가 가득 참, 생산자 대기
         * [put] 큐가 가득 참, 생산자 대기
         * [put] 큐가 가득 참, 생산자 대기
         */
//        producerFirst(queue); // 생산자 먼저 실행

        /**
         * == [소비자 먼저 실행] 시작, BoundedQueueV2 ==
         *
         * 소비자 시작
         * [소비 시도] ? <- []
         * [take] 큐에 데이터가 없음, 소비자 대기
         * [소비 시도] ? <- []
         * [소비 시도] ? <- []
         *
         * 현재 상태 출력, 큐 데이터: []
         * consumer1: TIMED_WAITING
         * consumer2: BLOCKED
         * consumer3: BLOCKED
         *
         * 생산자 시작
         * [생산 시도] data1 -> []
         * [생산 시도] data2 -> []
         * [생산 시도] data3 -> []
         *
         * 현재 상태 출력, 큐 데이터: []
         * consumer1: TIMED_WAITING
         * consumer2: BLOCKED
         * consumer3: BLOCKED
         * producer1: BLOCKED
         * producer2: BLOCKED
         * producer3: BLOCKED
         *
         * == [소비자 먼저 실행] 종료, BoundedQueueV2 ==
         *
         * [take] 큐에 데이터가 없음, 소비자 대기
         * [take] 큐에 데이터가 없음, 소비자 대기
         * [take] 큐에 데이터가 없음, 소비자 대기
         */
        consumerFirst(queue); // 소비자 먼저 실행
    }

    private static void producerFirst(BoundedQueue queue) {
        log("== [생산자 먼저 실행] 시작, " + queue.getClass().getSimpleName() + " == ");

        List<Thread> threads = new ArrayList<>();
        startProducer(queue, threads);
        printAllState(queue, threads);
        startConsumer(queue, threads);
        printAllState(queue, threads);
        log("== [생산자 먼저 실행] 종료, " + queue.getClass().getSimpleName() + " == ");
    }

    private static void consumerFirst(BoundedQueue queue) {
        log("== [소비자 먼저 실행] 시작, " + queue.getClass().getSimpleName() + " == ");

        List<Thread> threads = new ArrayList<>();
        startConsumer(queue, threads);
        printAllState(queue, threads);
        startProducer(queue, threads);
        printAllState(queue, threads);
        log("== [소비자 먼저 실행] 종료, " + queue.getClass().getSimpleName() + " == ");
    }

    private static void startProducer(BoundedQueue queue, List<Thread> threads) {
        System.out.println();
        log("생산자 시작");
        for (int i = 1; i <= 3; i++) {
            Thread producer = new Thread(new ProducerTask(queue, "data" + i), "producer" + i);
            threads.add(producer);
            producer.start();
            sleep(100);
        }
    }

    private static void startConsumer(BoundedQueue queue, List<Thread> threads) {
        System.out.println();
        log("소비자 시작");
        for (int i = 1; i <= 3; i++) {
            Thread consumer = new Thread(new ConsumerTask(queue), "consumer" + i);
            threads.add(consumer);
            consumer.start();
            sleep(100);
        }
    }

    private static void printAllState(BoundedQueue queue, List<Thread> threads) {
        System.out.println();
        log("현재 상태 출력, 큐 데이터: " + queue);
        for (Thread thread : threads) {
            log(thread.getName() + ": " + thread.getState());
        }
    }
}
