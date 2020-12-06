package sudokuhw;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.ArrayList;

/**
 *
 * @author Federico Bednarski 1589903
 */
public class Node {
    public int value;
    public ArrayList<Node> children = null;

    public Node(int v) {
        value = v;
    }
    
    public int childValue(int i) {
        return  children.get(i).value;
    }
    
    public void addChild(int childValue) {
        addChild(new Node(childValue));
    }
    
    public void addChild(Node child) {
        if(children == null)
            children = new ArrayList<>();
        children.add(child);
    }

    public long count() {
        if (children == null) {
            return 1;
        }
        long count = 0;
        
        for (Node child : children)
            count += child.count();
        
        return count;
    }

    public Node copy() {
        Node n = new Node(value);
        if (children != null) {
            n.children = new ArrayList<>(children.size());
            for (Node child : children)
                n.children.add(child);
        }
        return n;
    }

    public void addLeafs(ArrayList<Node> leafs) {
        if (children == null) {
            children = new ArrayList<>(leafs.size());
            for (Node leaf : leafs)
                children.add(leaf.copy());
        } else {
            for (Node child : children)
                child.addLeafs(leafs);
        }
    }
}
