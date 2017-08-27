package co.siempo.phone.receiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by Shahab on 5/26/2017.
 */

@SuppressWarnings("ALL")
public interface IDynamicStatus {
    public IntentFilter getIntentFilter();

    public void register(Context context);

    public void unregister(Context context);

    public void handleIntent(Context context, Intent intent);

}