package com.cisco.yangide.core.nature;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

import com.cisco.yangide.core.CoreUtil;
import com.cisco.yangide.core.YangCorePlugin;

public class YangProjectBuilder extends IncrementalProjectBuilder {

    // This has to match the "id" for the builder defined in the plugin.xml file.
    public static final String  BUILDER_ID  = "com.cisco.yangide.core.yangbuilder";
    
    @Override
    protected IProject[] build(int kind, Map<String, String> args, IProgressMonitor monitor) throws CoreException {
        YangCorePlugin.log(IStatus.INFO, "build: project.name[" + getProject().getName() +
                                                 "] kind[" + kind + "," + kindtoString(kind) + "] args[" + args + "] monitor[" + monitor + "]");
        if (kind == IncrementalProjectBuilder.AUTO_BUILD || kind == IncrementalProjectBuilder.INCREMENTAL_BUILD) {
            IResourceDelta  delta   = getDelta(getProject());
            if (delta != null) {
                YangCorePlugin.log(IStatus.INFO, "delta.kind[" + kind + "," + deltaKindToString(kind) + "]");
                incrementalBuild(delta, monitor);
            }
            else {
                fullBuild(monitor);
            }
        }
        else {
            fullBuild(monitor);
        }
        return null;
    }
    
    private void fullBuild(IProgressMonitor monitor) {
        try {
            getProject().accept(new YangProjectVisitor());
        }
        catch (CoreException ex) {
            YangCorePlugin.log(ex);
        }
    }

    private void incrementalBuild(IResourceDelta delta, IProgressMonitor monitor) {
        YangCorePlugin.log(IStatus.INFO, "incrementalBuild");
    }

    @Override
    protected void startupOnInitialize() {
        // add builder init logic here
        YangCorePlugin.log(IStatus.INFO, "startupOnInitialize: project.name[" + getProject().getName() + "]");
    }

    @Override
    protected void clean(IProgressMonitor monitor) {
        // add builder clean logic here
        YangCorePlugin.log(IStatus.INFO, "clean: project.name[" + getProject().getName() + "]");
    }
     
    public static String deltaKindToString(int kind) {
        Map<Integer, String>    deltaKindsMap   = new HashMap<>();
        deltaKindsMap.put(IResourceDelta.ADDED, "ADDED");
        deltaKindsMap.put(IResourceDelta.ADDED_PHANTOM, "ADDED_PHANTOM");
        deltaKindsMap.put(IResourceDelta.CHANGED, "CHANGED");
        deltaKindsMap.put(IResourceDelta.NO_CHANGE, "NO_CHANGE");
        deltaKindsMap.put(IResourceDelta.REMOVED, "REMOVED");
        deltaKindsMap.put(IResourceDelta.REMOVED_PHANTOM, "REMOVED_PHANTOM");

        StringBuilder   sb  = new StringBuilder();
        
        for (int key : deltaKindsMap.keySet()) {
            if ((key & kind) != 0) {
                if (sb.toString().length() > 0)
                    sb.append(" | ");
                sb.append(deltaKindsMap.get(key));
            }
        }
        if (sb.toString().length() == 0)
            sb.append("NO_CHANGE");
        return sb.toString();
    }
    
    public static String kindtoString(int kind) {
        switch (kind) {
        case    IncrementalProjectBuilder.AUTO_BUILD:           return "AUTO_BUILD";
        case    IncrementalProjectBuilder.CLEAN_BUILD:          return "CLEAN_BUILD";
        case    IncrementalProjectBuilder.FULL_BUILD:           return "FULL_BUILD";
        case    IncrementalProjectBuilder.INCREMENTAL_BUILD:    return "INCREMENTAL_BUILD";
        default:
            return "UNKNOWN";
        }
    }
    
    public static String resourceTypeToString(int resourceType) {
        switch (resourceType) {
        case    IResource.FILE:     return "FILE";
        case    IResource.FOLDER:   return "FOLDER";
        case    IResource.PROJECT:  return "PROJECT";
        case    IResource.ROOT:     return "ROOT";
        default: return "UNKNOWN";
        }
    }
    
    private static class YangProjectVisitor implements IResourceVisitor {
        @Override
        public boolean visit(IResource resource) throws CoreException {
            YangCorePlugin.log(IStatus.INFO, "visit: resource.name[" + resource.getName() +
                                                     "] type[" + resourceTypeToString(resource.getType()) +
                                                     "] fullpath[" + resource.getFullPath() + "]");
            if (resource.getType() == IResource.FILE && (CoreUtil.isYangLikeFileName(resource.getName()))) {
                YangCorePlugin.log(IStatus.INFO, "This is a Yang file which we will compile.");
            }
            
            boolean result  = false;
            if (resource.getType() == IResource.PROJECT || resource.getType() == IResource.FOLDER || resource.getType() == IResource.ROOT)
                result  = true;
            return result;
        }
    }
}
