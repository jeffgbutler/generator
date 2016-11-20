package org.mybatis.generator.eclipse.core.merge.visitors;

public enum CompilationUnitType {
    CLASS(true, true, false),
    INTERFACE(true, true, false),
    ENUM(false, false, true);
    
    private boolean canMergeWithClass;
    private boolean canMergeWithInterface;
    private boolean canMergeWithEnum;
    
    private CompilationUnitType(boolean canMergeWithClass ,boolean canMergeWithInterface, boolean canMergeWithEnum) {
        this.canMergeWithClass = canMergeWithClass;
        this.canMergeWithInterface = canMergeWithInterface;
        this.canMergeWithEnum = canMergeWithEnum;
    }
    
    public boolean canMergeWithClass() {
        return canMergeWithClass;
    }
    
    public boolean canMergeWithInterface() {
        return canMergeWithInterface;
    }
    
    public boolean canMergeWithEnum() {
        return canMergeWithEnum;
    }
}
