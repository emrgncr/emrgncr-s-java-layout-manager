package elayout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.swing.JPanel;

/**
 * A better layout manager that can change the component sizes dynamically<br>
 * ELayout has a {@link layoutAlignment} that defines in which way components are laid out
 * and a {@link spacingType} that defines how components are spaced. <br>
 * Each component is stored in the LinkedHashMap components with its {@link Constraint}
 * @author emrgncr
 *
 */
public class ELayout implements LayoutManager2 {
	/**
	 * Defines how a component is aligned in an {@link ELayout} {@link Constraint}.
	 * LEFT aligns the component to the left of the layout.
	 * CENTER centers the component.
	 * RIGHT aligns the components to the right of the layout.
	 * @author emrgncr
	 *
	 */
	public enum componentAlignment {
		LEFT,
		RIGHT,
		CENTER
	}
	/**
	 * Defines how a component size is interpreted. <br>In a  {@link Constraint}, component width and height are
	 * stored as numbers but also widthType and heightType are stored. If sizeType is ABSOLUTE, that number is regarded as 
	 * the absolute size. If sizeType is PERCENT, that number is regarded as the percentage to the container size. 
	 * <br>
	 * The SQUARE option can only be used on ONE of the width and height and fixes it to be the same as the other
	 * <br>
	 * the REST option just takes all the empty space left. There can only one REST component in one layout for that layout's {@linkplain layoutAlignment}.
	 * <br>
	 * The RATIO option is similar to SQUARE, but instead of fixing the size to be 1x1 it becomes 1xvalue where value is the number used in ratio
	 * <br>
	 * For example, if width = 70 and widthType = PERCENT, that components width is regarded as 70%
	 * <br>
	 * if width = 70, widthType = PERCENT, height = 1.2, heightType = RATIO: height is regarded as 1.2*width which is 84% of the width of the parent container.
	 * @author emrgncr
	 *
	 */
	public enum componentSizeType {
		PERCENT,
		ABSOLUTE,
		SQUARE,
		RATIO,
		REST,
	}
	/**
	 * Determines if the components are placed horizontally or vertically in the {@link ELayout}
	 * @author emrgncr
	 *
	 */
	public enum layoutAlignment{
		VERTICAL,
		HORIZONTAL
	}
	/**
	 * Spacing type of the {@link ELayout}.
	 * <br>
	 * Determines how is the empty space utilized in the container. <br><br>
	 * SPACE_AROUND: the empty space is divided to the number of components + 1,
	 * than that space is added to the top of the container, bottom of the container and between each component.<br><br>
	 * SPACE_BETWEEN: the empty space is divided to the number of components - 1,
	 * than that space is added between each component.<br><br>
	 * NO_SPACE: The empty space is not utilized. All components are put in either top, center or bottom of the container (for NO_SPACE_TOP, NO_SPACE_CENTER, NO_SPACE_TOP)
	 * @author emrgncr
	 *
	 */
	public enum spacingType{
		SPACE_AROUND,
		SPACE_BETWEEN,
		NO_SPACE_CENTER,
		NO_SPACE_TOP,
		NO_SPACE_BOT
	}
	
	
	/**
	 * String to be used in Constraint(String) constructor. Use this to pass JFrame.add(component,arg), as argument is string by default
	 * @param align {@linkplain componentAlignment} alignment of the component left/center/right
	 * @param leftMargin left margin of the component
	 * @param rightMargin right margin of the component
	 * @param topMargin top margin of the component
	 * @param botMargin bottom margin of the component
	 * @param widthType {@linkplain componentSizeType} width type of the component, percent or absolute.
	 * @param heightType {@linkplain componentSizeType} height type of the component, percent or absolute.
	 * @param width width of the component. IF width type is percent, this number is used as the percentage.
	 * @param height height of the component. IF height type is percent, this number is used as the percentage.
	 * maxWidth and maxHeight are infinite by default
	 * @return
	 */
	public static String constraintText(componentAlignment align, double leftMargin, double rightMargin, double topMargin,double botMargin, componentSizeType widthType, componentSizeType heightType, double width,double height) {
		return constraintText(align, leftMargin, rightMargin, topMargin, botMargin, widthType, heightType, width, height, Integer.MAX_VALUE, Integer.MAX_VALUE);
	}
	
	
	/**
	 * String to be used in Constraint(String) constructor. Use this to pass JFrame.add(component,arg), as argument is string by default (OR SO I THOUGH AFTER ONLY CHECKING THE SOURCE CODE OF BorderLayout :( )
	 * @param align {@linkplain componentAlignment} alignment of the component left/center/right
	 * @param leftMargin left margin of the component
	 * @param rightMargin right margin of the component
	 * @param topMargin top margin of the component
	 * @param botMargin bottom margin of the component
	 * @param widthType width type of the component, percent or absolute.
	 * @param heightType height type of the component, percent or absolute.
	 * @param width {@linkplain componentSizeType} width of the component. IF width type is percent, this number is used as the percentage.
	 * @param height {@linkplain componentSizeType} height of the component. IF height type is percent, this number is used as the percentage.
	 * @param maxWidth is the maximum width of the component
	 * @param maxHeight is the maximum height of the component
	 * @return
	 */
	public static String constraintText(componentAlignment align, double leftMargin, double rightMargin, double topMargin,double botMargin, componentSizeType widthType, componentSizeType heightType, double width,double height,int maxWidth, int maxHeight) {
		return align.name() + " " +  Double.toString(leftMargin) + " " + Double.toString(rightMargin) + " " + Double.toString(topMargin) + " " +  Double.toString(botMargin) + " " + 
			widthType.name() + " " +  heightType.name() + " " +  Double.toString(width) + " " +  Double.toString(height)+ " " +  Integer.toString(maxWidth)+ " " +  Integer.toString(maxHeight);
	}


	//the map that contains components and their constraints. Using linked hash map because component order is important
	private LinkedHashMap<Component,Constraint> components = new LinkedHashMap<>();
	//the direction this layout should operate in either horizontal or vertical
	private layoutAlignment alignDirection;
	private spacingType spacing;
	
	/**
	 * ELayout constructor. dafaults align direction to VERTICAL and spacing to No_SPACE_CENTER
	 */
	public ELayout() {
		this(layoutAlignment.VERTICAL,spacingType.NO_SPACE_CENTER);
	}
/**
 * ELayout constructor
 * @param alignDirection {@link layoutAlignment} the align direction, either horizontal or vertical 
 * @param spacing {@link spacingType} is the spacing typoe of the layout manager
 */
	public ELayout(layoutAlignment alignDirection,spacingType spacing) {
		super();
		this.alignDirection = alignDirection;
		this.spacing = spacing;
	}
	
	/**
	 * Adds an invisible and empty JPanel to the given container with designated width and height.<br>
	 * The container must be using a ELayout as its layout manager.
	 * @param c the component
	 * @param widthType {@linkplain componentSizeType}
	 * @param width
	 * @param heightType {@linkplain componentSizeType}
	 * @param height
	 * @return
	 * @throws IllegalArgumentException when the container is not using ELayout
	 */
	public static JPanel addEmptySpace(Container c,componentSizeType widthType, double width, componentSizeType heightType, double height) throws IllegalArgumentException{
		if(! (c.getLayout() instanceof ELayout)) {
			throw new IllegalArgumentException("the provided container's layout managar must be a ELayout");
		}
		else {
			JPanel ret = new JPanel();
			ret.setOpaque(false);
			c.add(ret,constraintText(componentAlignment.CENTER, 0, 0, 0, 0, widthType, heightType, width, height));
			return ret;
		}
	}
	
	
	
	
	/**
	 * get a components preferred dimensions by its constraints
	 * @param comp The component
	 * @param parent The parent container
	 * @return the Dimension of the object
	 * @throws IllegalArgumentException when the given component is not in the components map or there are multiple REST constraints in the layout direction
	 */
	private Dimension getComponentDimension(Component comp, Container parent) throws IllegalArgumentException {
		if(!components.containsKey(comp)) {
			throw new IllegalArgumentException("the component is not in the components map");
		}
		Constraint c = components.get(comp);
		//calculate preferred width
		
		double width = 0;
		if(c.getWidthType() == componentSizeType.PERCENT){
			Dimension parentSize = parent.getSize();
			width = Math.min(parentSize.getWidth()*c.getWidth()/100,c.maxWidth);
		}else
		if(c.getWidthType() == componentSizeType.ABSOLUTE) {
			width = c.getWidth(); 
			if (width < 0) {
				width = comp.getPreferredSize().getWidth();
			}
		} 
		
		
		double height = 0;
		if(c.getHeightType() == componentSizeType.PERCENT){
			Dimension parentSize = parent.getSize();
			height = Math.min(parentSize.getHeight()*c.getHeight()/100,c.maxHeight);
		}else 
		if(c.getHeightType() == componentSizeType.ABSOLUTE) {
			height = c.getHeight(); 
			if (height < 0) {
				height = comp.getPreferredSize().getHeight();
			}
		}
		
		
		//Square and ratio is calculated after percent and absolute is calculated
		if(c.getWidthType() == componentSizeType.SQUARE) width = height;
		if(c.getHeightType() == componentSizeType.SQUARE) height = width;
		if(c.getWidthType() == componentSizeType.RATIO) width = height*c.getWidth();
		if(c.getHeightType() == componentSizeType.RATIO) height = width*c.getHeight();
		
		//I really hope this does not destroy some things :/
		//REST is calculated after everything
		if(c.getWidthType() == componentSizeType.REST) {
		double add = c.getLeftMargin() + c.getRightMargin();
		//This here effectively makes this function quadratic :) nice
		for(Map.Entry<Component,Constraint> k : components.entrySet()) {
		if(k.getKey().equals(comp)) continue;
		if(k.getValue().getWidthType() == componentSizeType.REST && this.alignDirection == layoutAlignment.HORIZONTAL) throw new IllegalArgumentException("There can only be one REST components in one parent");
		add += getComponentDimension(k.getKey(), parent).getWidth()+ k.getValue().getLeftMargin() + k.getValue().getRightMargin();
		}
		width = Math.min(parent.getSize().getWidth() - add, c.maxWidth);
		}
		
		//same as above
		if(c.getHeightType() == componentSizeType.REST) {
		double add = c.getTopMargin() + c.getBotMargin();
		for(Map.Entry<Component,Constraint> k : components.entrySet()) {
		if(k.getKey().equals(comp)) continue;
		if(k.getValue().getHeightType() == componentSizeType.REST && this.alignDirection == layoutAlignment.VERTICAL) throw new IllegalArgumentException("There can only be one REST components in one parent");
		add += getComponentDimension(k.getKey(), parent).getHeight() + k.getValue().getTopMargin() + k.getValue().getBotMargin();
		}
		height = Math.min(parent.getSize().getHeight() - add, c.maxHeight);
		}
		
		
		return new Dimension((int)width,(int)height);
	}
	/**
	 * Returns a component's preferred dimensions including its margins calling {@linkplain}
	 * @param comp The component
	 * @param parent The parent container
	 * @return the Dimension of the object
	 * @throws IllegalArgumentException when the given component is not in the components map or there are multiple REST constraints in the layout direction
	 */
	Dimension getComponentDimensionMargins(Component comp, Container parent) throws IllegalArgumentException{
		Dimension d = getComponentDimension(comp, parent);
		Constraint c = components.get(comp);
		d.setSize(d.width + c.leftMargin + c.rightMargin, d.height + c.topMargin + c.botMargin);
		return d;
	}
	
	
	/**
	 * get a components preferred dimensions by its constraints
	 * @param comp
	 * @param parent
	 * @return
	 * @throws IllegalArgumentException when the given component is not in the components map
	 */
	private Dimension getComponentMinimumDimension(Component comp, Container parent) throws IllegalArgumentException {
		if(!components.containsKey(comp)) {
			throw new IllegalArgumentException("the component is not in the components map");
		}
		Constraint c = components.get(comp);
		//calculate preferred width
		double width = 0;
		if(c.getWidthType() == componentSizeType.ABSOLUTE) width = c.getWidth(); else {
			width = comp.getMinimumSize().getWidth();
		}
		double height = 0;
		if(c.getHeightType() == componentSizeType.ABSOLUTE) height = c.getHeight(); else {
			height = comp.getMinimumSize().getHeight();
		}
		return new Dimension((int)width,(int)height);
	}
	
	
	
	
	
	/**
	 * A LayoutManager method that is called when a new component is added or the parent container is revalidated/repainted (as far as I understood).
	 * 
	 * @param parent The parent container
	 */
	@Override
	public void layoutContainer(Container parent) {
		//Iterates over the components and sets their position and sizes.
		//TODO might be better to do this logic using streams
		synchronized(parent.getTreeLock()) {
			Insets insets = parent.getInsets();
			int top = insets.top;
			int bottom = parent.getHeight() - insets.bottom;
			int left = insets.left;
			int right = parent.getWidth() - insets.right;
			
			if(alignDirection == layoutAlignment.VERTICAL) {
				if(spacing == spacingType.SPACE_AROUND) {
					//If space around, there should be equal space between and around each component.
					double size_h = preferredLayoutSize(parent).getHeight();
					double excessHeight = Math.max(parent.getHeight() - size_h,0);
					int spaces = components.keySet().size() + 1;
					double eachSpace = excessHeight / spaces;
					double currentY = top;
					//now iterate over components to place them
					currentY += eachSpace;
					for(Map.Entry<Component, Constraint> e : components.entrySet()) {
						Component comp = e.getKey();
						Constraint c = e.getValue();
						Dimension cdim = getComponentDimension(comp, parent);
						currentY += c.topMargin;
						int pointX = left;
						//some maths here
						if(c.align == componentAlignment.LEFT) pointX = left + (int)c.leftMargin;
						else if(c.align == componentAlignment.RIGHT) pointX = right - (int)c.rightMargin - (int)cdim.getWidth();
						else if(c.align == componentAlignment.CENTER) {
							int center = (left + right) / 2;
							pointX = center - (int)(( cdim.getWidth() + c.leftMargin + c.rightMargin )/2);
						}
						comp.setBounds(pointX, (int)currentY, (int)cdim.getWidth(), (int)cdim.getHeight());
						currentY += c.botMargin + (int)cdim.getHeight();
						currentY += eachSpace;
						
					}
				}
				if(spacing == spacingType.SPACE_BETWEEN) {
					double size_h = preferredLayoutSize(parent).getHeight();
					double excessHeight = Math.max(parent.getHeight() - size_h,0);
					int spaces = components.keySet().size() - 1;
					double eachSpace = excessHeight / spaces;
					double currentY = top;
					//now iterate over components to place them
					for(Map.Entry<Component, Constraint> e : components.entrySet()) {
						Component comp = e.getKey();
						Constraint c = e.getValue();
						Dimension cdim = getComponentDimension(comp, parent);
						currentY += c.topMargin;
						int pointX = left;
						if(c.align == componentAlignment.LEFT) pointX = left + (int)c.leftMargin;
						else if(c.align == componentAlignment.RIGHT) pointX = right - (int)c.rightMargin - (int)cdim.getWidth();
						else if(c.align == componentAlignment.CENTER) {
							int center = (left + right) / 2;
							pointX = center - (int)(( cdim.getWidth() + c.leftMargin + c.rightMargin )/2);
						}
						comp.setBounds(pointX, (int)currentY, (int)cdim.getWidth(), (int)cdim.getHeight());
						currentY += c.botMargin + (int)cdim.getHeight();
						currentY += eachSpace;
						
					}
				}
				if(spacing == spacingType.NO_SPACE_CENTER || spacing == spacingType.NO_SPACE_BOT || spacing == spacingType.NO_SPACE_TOP) {
					double size_h = preferredLayoutSize(parent).getHeight();
					int vcenter = (top + bottom) / 2;
					double currentY = vcenter - (size_h/2);
					
					if(spacing == spacingType.NO_SPACE_TOP) {
						currentY = top;
					}
					else if(spacing == spacingType.NO_SPACE_BOT) {
						currentY = bottom - size_h;
					}
					
					
					//now iterate over components to place them
					for(Map.Entry<Component, Constraint> e : components.entrySet()) {
						Component comp = e.getKey();
						Constraint c = e.getValue();
						Dimension cdim = getComponentDimension(comp, parent);
						currentY += c.topMargin;
						int pointX = left;
						if(c.align == componentAlignment.LEFT) pointX = left + (int)c.leftMargin;
						else if(c.align == componentAlignment.RIGHT) pointX = right - (int)c.rightMargin - (int)cdim.getWidth();
						else if(c.align == componentAlignment.CENTER) {
							int center = (left + right) / 2;
							pointX = (int)(center - ( cdim.getWidth() + c.leftMargin + c.rightMargin )/2);
						}
						comp.setBounds(pointX, (int)currentY, (int)cdim.getWidth(), (int)cdim.getHeight());
						currentY += c.botMargin + (int)cdim.getHeight();
						
					}
				}
			
			}
			else if(alignDirection == layoutAlignment.HORIZONTAL) {
				if(spacing == spacingType.SPACE_AROUND) {
					double size_w= preferredLayoutSize(parent).getWidth();
					double excessWidth = Math.max(parent.getWidth() - size_w,0);
					int spaces = components.keySet().size() + 1;
					double eachSpace = excessWidth / spaces;
					double currentX = left;
					//now iterate over components to place them
					currentX += eachSpace;
					for(Map.Entry<Component, Constraint> e : components.entrySet()) {
						Component comp = e.getKey();
						Constraint c = e.getValue();
						Dimension cdim = getComponentDimension(comp, parent);
						currentX += c.leftMargin;
						int pointY = top;
						if(c.align == componentAlignment.LEFT) pointY = top + (int)c.topMargin;
						else if(c.align == componentAlignment.RIGHT) pointY = bottom - (int)c.botMargin - (int)cdim.getHeight();
						else if(c.align == componentAlignment.CENTER) {
							int center = (top + bottom) / 2;
							pointY = center - (int)(( cdim.getHeight() + c.botMargin + c.topMargin )/2);
						}
						comp.setBounds((int)currentX, (int)pointY, (int)cdim.getWidth(), (int)cdim.getHeight());
						currentX += c.rightMargin + (int)cdim.getWidth();
						currentX += eachSpace;
						
					}
				}
				if(spacing == spacingType.SPACE_BETWEEN) {
					double size_w= preferredLayoutSize(parent).getWidth();
					double excessWidth = Math.max(parent.getWidth() - size_w,0);
					int spaces = components.keySet().size() - 1;
					double eachSpace = excessWidth / spaces;
					double currentX = left;
					//now iterate over components to place them
					for(Map.Entry<Component, Constraint> e : components.entrySet()) {
						Component comp = e.getKey();
						Constraint c = e.getValue();
						Dimension cdim = getComponentDimension(comp, parent);
						currentX += c.leftMargin;
						int pointY = top;
						if(c.align == componentAlignment.LEFT) pointY = top + (int)c.topMargin;
						else if(c.align == componentAlignment.RIGHT) pointY = bottom - (int)c.botMargin - (int)cdim.getHeight();
						else if(c.align == componentAlignment.CENTER) {
							int center = (top + bottom) / 2;
							pointY = center - (int)(( cdim.getHeight() + c.botMargin + c.topMargin )/2);
						}
						comp.setBounds((int)currentX, (int)pointY, (int)cdim.getWidth(), (int)cdim.getHeight());
						currentX += c.rightMargin + (int)cdim.getWidth();
						currentX += eachSpace;
						
					}
				}
				if(spacing == spacingType.NO_SPACE_CENTER || spacing == spacingType.NO_SPACE_BOT || spacing == spacingType.NO_SPACE_TOP) {
					double size_h = preferredLayoutSize(parent).getWidth();
					int vcenter = (left + right) / 2;
					double currentX = vcenter - (size_h/2);
					
					if(spacing == spacingType.NO_SPACE_TOP) {
						currentX = left;
					}
					else if(spacing == spacingType.NO_SPACE_BOT) {
						currentX = right - size_h;
					}
					
					
					//now iterate over components to place them
					for(Map.Entry<Component, Constraint> e : components.entrySet()) {
						Component comp = e.getKey();
						Constraint c = e.getValue();
						Dimension cdim = getComponentDimension(comp, parent);
						currentX += c.leftMargin;
						int pointY= top;
						if(c.align == componentAlignment.LEFT) pointY = top + (int)c.topMargin;
						else if(c.align == componentAlignment.RIGHT) pointY = bottom - (int)c.botMargin - (int)cdim.getHeight();
						else if(c.align == componentAlignment.CENTER) {
							int center = (top + bottom) / 2;
							pointY = center - (int)(( cdim.getHeight() + c.topMargin + c.botMargin )/2);
						}
						comp.setBounds((int)currentX, (int)pointY, (int)cdim.getWidth(), (int)cdim.getHeight());
						currentX += c.rightMargin + (int)cdim.getWidth();
						
					}
				}
			}
			
			
		}
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		//calculate the preferred layout size
		double height = 0;
		double width = 0;
		if (alignDirection == layoutAlignment.VERTICAL) {
			//if direction is vertical add heights, width is the max width
			for (Map.Entry<Component, Constraint> e : components.entrySet()) {
				Constraint c = e.getValue();
				Component p = e.getKey();
				Dimension componentSize = getComponentMinimumDimension(p, parent);
				width = Math.max(width, c.leftMargin + c.rightMargin + componentSize.getWidth());
				height += c.topMargin + c.botMargin + componentSize.getHeight();
			}
			return new Dimension((int)width,(int)height);
		}else {
			for (Map.Entry<Component, Constraint> e : components.entrySet()) {
				Constraint c = e.getValue();
				Component p = e.getKey();
				Dimension componentSize = getComponentMinimumDimension(p, parent);
				height = Math.max(height, c.topMargin + c.botMargin + componentSize.getHeight());
				width += c.leftMargin + c.rightMargin + componentSize.getWidth();
			}
			return new Dimension((int)width,(int)height);
		}
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		//calculate the preferred layout size
		double height = 0;
		double width = 0;
		if (alignDirection == layoutAlignment.VERTICAL) {
			//if direction is vertical add heights, width is the max width
			for (Map.Entry<Component, Constraint> e : components.entrySet()) {
				Constraint c = e.getValue();
				Component p = e.getKey();
				Dimension componentSize = getComponentDimension(p, parent);
				width = Math.max(width, c.leftMargin + c.rightMargin + componentSize.getWidth());
				height += c.topMargin + c.botMargin + componentSize.getHeight();
			}

			
			return new Dimension((int)width,(int)height);
		}else {
			for (Map.Entry<Component, Constraint> e : components.entrySet()) {
				Constraint c = e.getValue();
				Component p = e.getKey();
				Dimension componentSize = getComponentDimension(p, parent);
				height = Math.max(height, c.topMargin + c.botMargin + componentSize.getHeight());
				width += c.leftMargin + c.rightMargin + componentSize.getWidth();
			}
			return new Dimension((int)width,(int)height);
		}
	}

	@Override
	public void removeLayoutComponent(Component comp) {
		synchronized (comp.getTreeLock()) {
		if(components.containsKey(comp)) {
			components.remove(comp);
		}
		}
	}
	/**
	 * Get a component's {@link Constraint} object
	 * @param comp Component
	 * @return
	 */
	public Constraint getComponentConstraint(Component comp) {
		if(components.containsKey(comp)) {
			return components.get(comp);
		}
		return null;
	}


	/**
	 * Add a component to the {@linkplain ELayout}<br>
	 * @param name The string that has the constraint info. This string must be created using <code>ELayout.consttraintText(...)</code>
	 * or formatted as intended.
	 */
	@Override
	public void addLayoutComponent(String name, Component comp) {
		synchronized (comp.getTreeLock()) {
			if(name == null ||  name == "") {
				components.put(comp, new Constraint(componentAlignment.CENTER, 0, 0, 0, 0, 
						componentSizeType.ABSOLUTE,componentSizeType.ABSOLUTE, comp.getPreferredSize().getWidth(), comp.getPreferredSize().getHeight()));
				return;
			}
			components.put(comp, new Constraint(name));
		}
	}

	/**
	 * 
	 * @param constraints The constraints must be a {@link elayout.Constraint} object.
	 */
	@Override
	public void addLayoutComponent(Component comp, Object constraints) {
		if (constraints instanceof Constraint) {
			Constraint constraint = (Constraint) constraints;
			components.put(comp, constraint);
		}
		
	}


	@Override
	public Dimension maximumLayoutSize(Container target) {
		Stream<Dimension> s = components.keySet().parallelStream().map((Component e)->{
			return getComponentDimensionMargins(e, target);
		});
		IntStream ws = s.mapToInt(x -> x.width);
		IntStream hs = s.mapToInt(x -> x.height);
		return new Dimension(ws.sum(),hs.sum());
	}


	@Override
	public float getLayoutAlignmentX(Container target) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public float getLayoutAlignmentY(Container target) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public void invalidateLayout(Container target) {
		//TODO Auto-generated method stub
	}

}
