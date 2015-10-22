package barqsoft.footballscores;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * Created by iluz on 21/10/15.
 */
public class ScoresWidgetProvider extends AppWidgetProvider {
    public static final String EXTRA_LIST_VIEW_ROW_NUMBER = "barqsoft.footballscores.widget.listview.item_position";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_scores);

            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.widget_scores, pendingIntent);

            views.setRemoteAdapter(R.id.widget_scores_list,
                    new Intent(context, ScoresWidgetRemoteViewsService.class));

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
