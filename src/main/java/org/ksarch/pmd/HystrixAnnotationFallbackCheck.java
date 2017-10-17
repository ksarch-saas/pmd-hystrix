package org.ksarch.pmd;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.java.rule.*;

public class HystrixAnnotationFallbackCheck extends AbstractJavaRule {
    public Object visit(ASTNormalAnnotation node, Object data) {
        ASTName name = (ASTName) node.jjtGetChild(0);
        String annotation, key;
        if (name.getImage().equals("HystrixCommand")) {
            annotation = "HystrixCommand";
            key = "fallbackMethod";
        } else if (name.getImage().equals("FeignClient")) {
            annotation = "FeignClient";
            key = "fallback";
        } else {
            return super.visit(node, data);
        }

        List<ASTMemberValuePair> pairs = new ArrayList<ASTMemberValuePair>();
        node.findDescendantsOfType(ASTMemberValuePair.class, pairs, true);

        boolean flag = false;
        for(ASTMemberValuePair p : pairs) {
            if (p.getImage().equals(key)) {
                flag = true;
                break;
            }
        }

        if (!flag) {
            addViolation(data, node);
        }

        return data;
    }

    public Object visit(ASTMarkerAnnotation node, Object data) {
        if(!(node.jjtGetChild(0) instanceof  ASTName)) {
            return data;
        }

        ASTName name = (ASTName) node.jjtGetChild(0);
        if(name.getImage().equals("HystrixCommand") || name.getImage().equals("FeignClient")) {
            addViolation(data, node);
        }
        return data;
    }
    /*private Object check(AbstractNode node, Object data, String annotation, String key) {
        List <ASTNormalAnnotation> annotations_normal = new ArrayList<ASTNormalAnnotation>();
        node.findDescendantsOfType(ASTNormalAnnotation.class, annotations_normal, true);

        for (ASTNormalAnnotation a : annotations_normal) {
            if (!(a.jjtGetChild(0) instanceof ASTName)) {
                continue;
            }

            ASTName name = (ASTName) a.jjtGetChild(0);
            System.out.println(name.getImage() + annotation);
            if (!name.getImage().equals(annotation)) {
                continue;
            }

            List<ASTMemberValuePair> pairs = new ArrayList<ASTMemberValuePair>();
            a.findDescendantsOfType(ASTMemberValuePair.class, pairs, true);

            System.out.println("VVV" + name.getImage());
            boolean flag = false;
            for(ASTMemberValuePair p : pairs) {
                if (p.getImage().equals(key)) {
                    flag = true;
                    break;
                }
            }

            if (!flag) {
                addViolation(data, node);
            }
        }

        List <ASTMarkerAnnotation> annotations_mark = new ArrayList<ASTMarkerAnnotation>();
        node.findDescendantsOfType(ASTMarkerAnnotation.class, annotations_mark, true);

        for (ASTMarkerAnnotation a : annotations_mark) {
            if(!(a.jjtGetChild(0) instanceof  ASTName)) {
                continue;
            }

            ASTName name = (ASTName) a.jjtGetChild(0);
            if(name.getImage().equals(annotation)) {
                addViolation(data, node);
            }
        }
        return data;
    }


    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        return this.check(node, data, "HystrixCommand", "fallbackMethod");
    }

    public Object visit(ASTTypeDeclaration node, Object data) {
        return this.check(node, data, "FeignClient", "fallback");
    }*/
}
