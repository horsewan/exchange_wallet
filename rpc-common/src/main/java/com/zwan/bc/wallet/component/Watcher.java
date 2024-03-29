package com.zwan.bc.wallet.component;

import com.zwan.bc.wallet.entity.Coin;
import com.zwan.bc.wallet.entity.Deposit;
import com.zwan.bc.wallet.event.DepositEvent;
import com.zwan.bc.wallet.service.WatcherLogService;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Data
public abstract class Watcher implements Runnable{
    private Logger logger = LoggerFactory.getLogger(Watcher.class);
    private boolean stop = false;
    //默认同步间隔5秒
    private Long checkInterval = 5000L;
    private Long currentBlockHeight = 0L;
    private int step = 5;
    //区块确认数
    private int confirmation = 1;
    private DepositEvent depositEvent;
    private Coin coin;
    private WatcherLogService watcherLogService;

    public void check(){
        try {
            Long networkBlockNumber = getNetworkBlockHeight() - confirmation + 1;
            if (currentBlockHeight < networkBlockNumber) {
                long startBlockNumber = currentBlockHeight + 1;
                currentBlockHeight = (networkBlockNumber - currentBlockHeight > step) ? currentBlockHeight + step : networkBlockNumber;
                logger.info("replay block from {} to {}", startBlockNumber, currentBlockHeight);
                List<Deposit> deposits = replayBlock(startBlockNumber, currentBlockHeight);
                deposits.forEach(deposit -> {
                    depositEvent.onConfirmed(deposit);
                });
                //记录日志
                watcherLogService.update(coin.getName(),currentBlockHeight);
            } else {
                logger.info("already latest height {},nothing to do!", currentBlockHeight);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public abstract List<Deposit> replayBlock(Long startBlockNumber, Long endBlockNumber);

    public abstract Long getNetworkBlockHeight();

    @Override
    public void run() {
        stop = false;
        long nextCheck = 0;
        while(!(Thread.interrupted() || stop)) {
            if (nextCheck <= System.currentTimeMillis()) {
                try {
                    nextCheck = System.currentTimeMillis() + checkInterval;
                    logger.info("check...");
                    check();
                } catch (Exception ex) {
                    logger.info(ex.getMessage());
                }
            }
            else {
                try {
                    Thread.sleep(Math.max(nextCheck - System.currentTimeMillis(), 100));
                } catch (InterruptedException ex) {
                    logger.info(ex.getMessage());
                }
            }
        }
    }
}
