package org.ksarch.pmd;

import java.util.ArrayList;
import java.util.List;
import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.java.rule.*;

public class HystrixFallbackCheck extends AbstractJavaRule {
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        ASTExtendsList n = node.getFirstDescendantOfType(ASTExtendsList.class);
        if (n == null) {
            /* not sub-class */
            return data;
        }

        List<ASTClassOrInterfaceType> ext_list = n.findChildrenOfType(ASTClassOrInterfaceType.class);
        if (ext_list.isEmpty()) {
            /* not HystrixCommand */
            return data;
        }

        boolean flag = false;
        for(ASTClassOrInterfaceType t : ext_list) {
            if (t.getImage().equals("HystrixCommand")) {
                flag = true;
                break;
            }
        }

        if (!flag) {
            /* not sub-class of HystrixCommand */
            return data;
        }

        /* find method fallback */
        List <ASTMethodDeclaration> methods = new ArrayList<ASTMethodDeclaration>();
        node.findDescendantsOfType(ASTMethodDeclaration.class, methods, true);
        flag = false;
        for(ASTMethodDeclaration m : methods) {
            if (m.getMethodName().equals("getFallback")) {
                /* validate */
                flag = true;
                break;
            }
        }

        if (!flag) {
            addViolation(data, node);
        }
        return data;
    }
}
