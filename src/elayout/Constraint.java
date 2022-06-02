package elayout;

import elayout.ELayout.componentAlignment;
import elayout.ELayout.componentSizeType;


/**
 * A class for ELayout that holds component properties.
 * Each constraint has:<br>
 * a {@linkplain componentAlignment} align that defines how that component is aligned in the container,<br>
 * four doubles leftMargin, rightMargin, topMargin, botMargin that define ABSOLUTE sized blank spaces around the component, <br>
 * {@linkplain componentSizeType} widthType and heightType that define how width and height values of the component is interpreted, <br>
 * double width and height,
 * integer maxWidth and maxHeight.
 * 
 * @author emrgncr
 *
 */
public class Constraint implements Cloneable{
	protected class ConstraintArgumentsWrongException extends IllegalArgumentException{
		private static final long serialVersionUID = 2866211915569556244L;
		protected ConstraintArgumentsWrongException(String s) {
			super(s);
		}
		
	}
	 double leftMargin = 0;
	 componentAlignment align = componentAlignment.CENTER;
	 double rightMargin = 0;
	 double topMargin = 0;
	 double botMargin = 0;
	 componentSizeType widthType = componentSizeType.PERCENT;
	 componentSizeType heightType = componentSizeType.ABSOLUTE;
	 double width = 70;
	 double height = 100;
	 int maxWidth = Integer.MAX_VALUE;
	 int maxHeight = Integer.MAX_VALUE;
	/**
	 * Constraint constructor. This constructor should not be directly called. This constructor is automatically called when <code> JFrame.add(component,string)</code> method is called. In an ELayout, when adding a component that string field should NOT be filled with an arbitrary string and only should be filled using Elayout.constraintText(...)<br>Example:<br>
	 * <code>
	 * this.add(button,ELayout.constraintText(componentAlignment.CENTER, 0, 0, 0, 0, componentSizeType.ABSOLUTE,componentSizeType.ABSOLUTE, button.getPreferredSize().getWidth(), button.getPreferredSize().getHeight());
	 * </code><br>
	 * These values are also the default values if no string is passed.
	 * <br>
	 * Some optional parameters can also added by adding them to the string with a space.
	 * <br>
	 * Optional parameters: <br>
	 * dynamicFontSize --> if the component is an instance of JTextPane, its font size is changed dynamically so it fits in one line 
	 * @param constraint the string
	 * @throws ConstraintArgumentsWrongException when the string provided is formatted wrongly (usually because the string is not generated with ELayout.constraintText(...))
	 */
	public Constraint(String constraint) throws ConstraintArgumentsWrongException{
		try {
		String[] args = constraint.split(" ");
		this.align = componentAlignment.valueOf(args[0]);
		this.leftMargin = Double.parseDouble(args[1]);
		this.rightMargin = Double.parseDouble(args[2]);
		this.topMargin = Double.parseDouble(args[3]);
		this.botMargin = Double.parseDouble(args[4]);
		this.widthType = componentSizeType.valueOf(args[5]);
		this.heightType = componentSizeType.valueOf(args[6]);
		this.width = Double.parseDouble(args[7]);
		this.height = Double.parseDouble(args[8]);
		this.setMaxWidth(Integer.parseInt(args[9]));
		this.setMaxHeight(Integer.parseInt(args[10]));
		}catch(Exception e) {
			throw new ConstraintArgumentsWrongException("The arguments provided for the constraint is formatted incorrectly.");
		}
	}
	public Constraint(componentAlignment align, double leftMargin, double rightMargin, double topMargin,double botMargin
			, componentSizeType widthType, componentSizeType heightType, double width,double height,int maxWidth, int maxHeight) {
		this.align = align;
		this.leftMargin = leftMargin;
		this.rightMargin = rightMargin;
		this.topMargin = topMargin;
		this.botMargin = botMargin;
		this.widthType = widthType;
		this.heightType = heightType;
		this.width = width;
		this.height = height;
		this.setMaxWidth(maxWidth);
		this.setMaxHeight(maxHeight);
	}
	public Constraint(componentAlignment align, double leftMargin, double rightMargin, double topMargin,double botMargin,
			componentSizeType widthType, componentSizeType heightType, double width,double height) {
		this(align, leftMargin, rightMargin, topMargin, botMargin,
			widthType, heightType, width, height,Integer.MAX_VALUE,Integer.MAX_VALUE);
	}
	
	@Override
	public Constraint clone() {
		return new Constraint(align, leftMargin, rightMargin, topMargin, botMargin, widthType, heightType, width, height, maxWidth, maxHeight);
	}
		
	public componentAlignment getAlign() {
		return align;
	}
	public void setAlign(componentAlignment align) {
		this.align = align;
	}
	public double getLeftMargin() {
		return leftMargin;
	}
	public void setLeftMargin(double leftMargin) {
		this.leftMargin = leftMargin;
	}
	public double getRightMargin() {
		return rightMargin;
	}
	public void setRightMargin(double rightMargin) {
		this.rightMargin = rightMargin;
	}
	public double getTopMargin() {
		return topMargin;
	}
	public void setTopMargin(double topMargin) {
		this.topMargin = topMargin;
	}
	public double getBotMargin() {
		return botMargin;
	}
	public void setBotMargin(double botMargin) {
		this.botMargin = botMargin;
	}
	public componentSizeType getWidthType() {
		return widthType;
	}
	public void setWidthType(componentSizeType widthType) {
		this.widthType = widthType;
	}
	public componentSizeType getHeightType() {
		return heightType;
	}
	public void setHeightType(componentSizeType heightType) {
		this.heightType = heightType;
	}
	public double getWidth() {
		return width;
	}
	public void setWidth(double width) {
		this.width = width;
	}
	public double getHeight() {
		return height;
	}
	public void setHeight(double height) {
		this.height = height;
	}
	public int getMaxWidth() {
		return maxWidth;
	}
	public void setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
	}
	public int getMaxHeight() {
		return maxHeight;
	}
	public void setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
	}


	
	
}