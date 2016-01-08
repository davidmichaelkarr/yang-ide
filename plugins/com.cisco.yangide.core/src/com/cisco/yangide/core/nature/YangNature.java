package com.cisco.yangide.core.nature;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

public class YangNature implements IProjectNature {
    private IProject project;
    
    // This needs to match the nature ID in the plugin.xml file.
    public static final String  NATURE_ID   = "com.cisco.yangide.core.yangnature";
    
    @Override
    public void configure() throws CoreException {
        IProjectDescription desc        = project.getDescription();
        ICommand[]          commands    = desc.getBuildSpec();

        boolean found = false;
        for (int i = 0; i < commands.length; ++i) {
           if (commands[i].getBuilderName().equals(YangProjectBuilder.BUILDER_ID)) {
              found = true;
              break;
           }
        }
        
        if (!found) { 
           ICommand command = desc.newCommand();
           command.setBuilderName(YangProjectBuilder.BUILDER_ID);
           ICommand[] newCommands = new ICommand[commands.length + 1];

           // Add it before other builders.
           System.arraycopy(commands, 0, newCommands, 1, commands.length);
           newCommands[0] = command;
           desc.setBuildSpec(newCommands);
           project.setDescription(desc, null);
        }
    }

    @Override
    public void deconfigure() throws CoreException {
        // TODO Auto-generated method stub
        // Remove the YangBuilder from the project.
    }

    @Override
    public IProject getProject() { return project; }

    @Override
    public void setProject(IProject project) {
        this.project    = project;
    }
}