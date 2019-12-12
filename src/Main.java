import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import de.ur.mi.pluginhelper.User.User;
import de.ur.mi.pluginhelper.logger.Log;
import de.ur.mi.pluginhelper.logger.LogDataType;
import de.ur.mi.pluginhelper.logger.LogManager;
import de.ur.mi.pluginhelper.logger.SyncProgressListener;
import de.ur.mi.pluginhelper.tasks.TaskConfiguration;
import de.ur.mi.pluginhelper.ui.UserDialogManager;
import de.ur.mi.pluginhelper.ui.UserResponse;

import javax.swing.*;

public class Main implements ProjectComponent {

    private Project project;
    private TaskConfiguration task;
    private User user;
    private Log log;

    public Main(Project project) {
        this.project = project;
        user = User.getLocalUser();
        log = LogManager.openLog(user.getSessionID(), "MA-Fischer");
        log.log(user.getSessionID(), LogDataType.USER, "log", "User loaded from local file (ID = " + user.getID() + ")");

        JFrame frame = new JFrame("TEst");
        frame.setSize(500,500);
        frame.setVisible(true);
    }

    @Override
    public void initComponent() {
        log.log(user.getSessionID(), LogDataType.PLUGIN, "lifecycle", "Plugin initialized");
    }

    @Override
    public void projectOpened() {

        String serverUrl = "http://regensburger-forscher.de:9999/upload/";
        task = TaskConfiguration.loadFrom(project);
        log.log(user.getSessionID(), LogDataType.IDE, "project", "Project opened");
        UserResponse response = UserDialogManager.showConfirmationDialog("Möchten Sie die Logdatei (" + log.getLogFile().getName() + ") jetzt auf den Server " + serverUrl + " hochladen?", "Upload der Logdatei");
        if (response == UserResponse.ACCEPT) {
            LogManager.syncLog(log, user, serverUrl, new SyncProgressListener() {
                @Override
                public void onFinished() {
                    System.out.println("Upload finished");
                }
                @Override
                public void onFailed() {
                    System.out.println("Upload failed");
                }
            });
        }
    }

    @Override
    public void projectClosed() {
        log.log(user.getSessionID(), LogDataType.IDE, "project", "Project closed");
    }

}


