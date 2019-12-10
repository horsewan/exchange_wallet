package com.zwan.bc.wallet.util;

import com.zwan.bc.wallet.entity.Account;

public interface AccountReplayListener {

    void replay(Account account);
}
