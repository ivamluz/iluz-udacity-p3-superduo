package barqsoft.footballscores.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;

/**
 * Created by iluz on 21/10/15.
 */
public class ScoresWidgetProvider extends AppWidgetProvider {
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
            views.setEmptyView(R.id.widget_scores_list, R.id.widget_empty_list_item);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
