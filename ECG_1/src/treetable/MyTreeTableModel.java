/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package treetable;

/**
 *
 * @author bon
 */
import java.util.ArrayList;
import java.util.List;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;


public class MyTreeTableModel extends AbstractTreeTableModel 
{
	private MyTreeNode myroot;
	
	public MyTreeTableModel()
	{
		myroot = new MyTreeNode();
		
		MyTreeNode measurement = new MyTreeNode( "Measurement", 
		  "" );
		measurement.getChildren().add( new MyTreeNode( "Heart rate (bpm)", 
		  "60" ) );
		measurement.getChildren().add( new MyTreeNode( "RR interval (s)", 
		  "1" ) );
                measurement.getChildren().add( new MyTreeNode( "Atrial rate (bpm)", 
		  "61" ) );
                measurement.getChildren().add( new MyTreeNode( "P wave duration (s)", 
		  "1" ) );
                measurement.getChildren().add( new MyTreeNode( "PR interval (s)", 
		  "1" ) );
                measurement.getChildren().add( new MyTreeNode( "Q onset (s)", 
		  "1" ) );
                measurement.getChildren().add( new MyTreeNode( "QRS duration (s)", 
		  "1" ) );
                measurement.getChildren().add( new MyTreeNode( "QT interval (s)", 
		  "1" ) );
                measurement.getChildren().add( new MyTreeNode( "QTCB (s)", 
		  "1" ) );
                measurement.getChildren().add( new MyTreeNode( "QTCF (s)", 
		  "1" ) );
		myroot.getChildren().add( measurement );
                
                                MyTreeNode interpretation = new MyTreeNode( "Interpretation", 
		  "" );
		interpretation.getChildren().add( new MyTreeNode( "MD signature", 
		  "อ.นพ. ปิยพงษ์ คำริน" ) );
		interpretation.getChildren().add( new MyTreeNode( "Date", 
		  "25 July 2012" ) );
                                interpretation.getChildren().add( new MyTreeNode( "Time", 
		  "08:23:45" ) );
                                MyTreeNode statements = new MyTreeNode( "Statements", 
		  "" );
                                statements.getChildren().add( new MyTreeNode( "1", 
		  "Sinus rhythm") );
                                statements.getChildren().add( new MyTreeNode( "2", 
		  "normal P axis") );
                                statements.getChildren().add( new MyTreeNode( "3", 
		  "V-rate 50-99" ) );
                                interpretation.getChildren().add(statements);
                                interpretation.getChildren().add( new MyTreeNode( "Diagnosis", 
		  "Normal sinus rhythm" ) );
		myroot.getChildren().add( interpretation );


	}

	@Override
	public int getColumnCount() 
	{
		return 2;
	}
	
	@Override
	public String getColumnName( int column )
	{
		switch( column )
		{
		case 0: return "Name";
		case 1: return "Value";
		default: return "Unknown";
		}
	}

	@Override
	public Object getValueAt( Object node, int column ) 
	{
		//System.out.println( "getValueAt: " + node + ", " + column );
		MyTreeNode treenode = ( MyTreeNode )node;
		switch( column )
		{
		case 0: return treenode.getName();
		case 1: return treenode.getDescription();
		case 2: return treenode.getChildren().size();
		default: return "Unknown";
		}
	}

	@Override
	public Object getChild( Object node, int index ) 
	{
		MyTreeNode treenode = ( MyTreeNode )node;
		return treenode.getChildren().get( index );
	}

	@Override
	public int getChildCount( Object parent ) 
	{
		MyTreeNode treenode = ( MyTreeNode )parent;
		return treenode.getChildren().size();
	}

	@Override
	public int getIndexOfChild( Object parent, Object child ) 
	{
		MyTreeNode treenode = ( MyTreeNode )parent;
		for( int i=0; i>treenode.getChildren().size(); i++ )
		{
			if( treenode.getChildren().get( i ) == child )
			{
				return i;
			}
		}

		return 0;
	}
	
	 public boolean isLeaf( Object node )
	 {
		 MyTreeNode treenode = ( MyTreeNode )node;
		 if( treenode.getChildren().size() > 0 )
		 {
			 return false;
		 }
		 return true;
	 }
	 
	 @Override
	 public Object getRoot()
	 {
		 return myroot;
	 }
}

