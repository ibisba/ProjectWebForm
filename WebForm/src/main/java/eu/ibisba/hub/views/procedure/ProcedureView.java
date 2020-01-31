package eu.ibisba.hub.views.procedure;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import eu.ibisba.hub.MainView;
import eu.ibisba.hub.backend.BackendService;
import eu.ibisba.hub.backend.IBISBASubmission;
import eu.ibisba.hub.backend.ISA.Assay;
import eu.ibisba.hub.backend.ISA.Investigation;
import eu.ibisba.hub.backend.ISA.Study;
import eu.ibisba.hub.backend.Workflow;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@Route(value = "procedure", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@PageTitle("Procedure")
@CssImport("styles/views/masterdetail/master-detail-view.css")
public class ProcedureView extends Div {
    public ProcedureView() {
        setId("master-detail-view");

        BackendService backendService = new BackendService();
        // Get all the objects for ISA from XLSX
        ArrayList<Investigation> investigations = backendService.getWorkflows();

        // Initiate the gird
        Grid<Workflow> grid = new Grid<>(Workflow.class);
        // Initiate the final grid
        Grid<Workflow> gridSelection = new Grid<>(Workflow.class);
        Set<Workflow> selectedItems = new HashSet<>();
        gridSelection.setItems(selectedItems);

        // Translating the ISA objects to a grid compatible overview
        ArrayList<Workflow> workflows = populateWorkflow(investigations);

        // The dataprovider for lazy loading containing all the workflows
        ListDataProvider<Workflow> dataProvider = new ListDataProvider<>(workflows);
        // Lazy loading data provider (need to check if the selected rows are known otherwise use the field content
        grid.setDataProvider(dataProvider);

        makeGrid(grid, dataProvider);
        // Check if this goes ok...
        makeGrid(gridSelection, dataProvider);


        // Text field
        HorizontalLayout horizontalLayoutField = new HorizontalLayout();
        horizontalLayoutField.setMaxWidth("100%");
        TextArea textArea = new TextArea("Element codes");
        textArea.setPlaceholder("Place identifiers here for reuse");
        textArea.setMaxWidth("100%");

        textArea.addValueChangeListener(textAreaStringComponentValueChangeEvent -> {
            System.err.println("change listener activated");
           // When value is changed... not sure if button which changes the value will trigger this event
            // erase all selected items and add all obtained
            selectedItems.clear();
            selectedItems.addAll(getWorkflowsOrder(textAreaStringComponentValueChangeEvent.getValue().split(","), workflows));
            // Refresh grid as new items have been added
            gridSelection.getDataProvider().refreshAll();
        });

        Button addButton = new Button();
        addButton.setText("Add to plan");

        // Update content upon grid selection
        addButton.addClickListener(buttonClickEvent -> {
            selectedItems.addAll(orderGrid(workflows, grid.getSelectedItems()));
            gridSelection.getDataProvider().refreshAll();
            String message = "";
            for (Workflow workflow : selectedItems) {
                message += workflow.getAssay().getIdentifier() + ",";
            }
            textArea.setValue(message);
        });

        Button removeButton = new Button();
        removeButton.setText("Remove from plan");

        // TODO fix according to original order
        removeButton.addClickListener(buttonClickEvent -> {
            selectedItems.removeAll(gridSelection.getSelectedItems());
            gridSelection.getDataProvider().refreshAll();
            String message = "";
            for (Workflow workflow : selectedItems) {
                message += workflow.getAssay().getIdentifier() + ",";
            }
            textArea.setValue(message);
        });

        // Add button to submit
        Button submitButton = new Button();
        submitButton.setText("Submit to IBISBA-HUB");
        submitButton.addClickListener(buttonClickEvent -> {
            IBISBASubmission.submission(workflows, textArea.getValue());
        });

        // Layout
        VerticalLayout verticalLayout = new VerticalLayout();
        // Button layout
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidthFull();
        horizontalLayout.add(submitButton, addButton, removeButton);
        horizontalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        // Grid with buttons in between layout
        verticalLayout.add(grid, horizontalLayout, gridSelection, textArea);
        add(verticalLayout);
        // add(verticalLayout, submitButton, addButton, removeButton);
    }

    /**
     * Function to reorder the selected items according to the workflows list
     * @param workflows the original set of workflows according to the ISA object
     * @param selectedItems the selected set of items from the workflow
     * @return the reorder selection
     */
    private Collection<? extends Workflow> orderGrid(ArrayList<Workflow> workflows, Set<Workflow> selectedItems) {
        ArrayList<String> identifiers = new ArrayList<>();
        ArrayList<Workflow> selection = new ArrayList<>();
        for (Workflow item : selectedItems) {
            identifiers.add(String.valueOf(item.getAssay().getIdentifier()));
        }

        for (Workflow workflow : workflows) {
            String identifier = String.valueOf(workflow.getAssay().getIdentifier());
            if (identifiers.contains(identifier)) {
                // TODO see if we can preserve the submission procedure not the identifier order as selected as these are "random"
                selection.add(workflow);
            }
        }
        return selection;
    }

    private Set<Workflow> getWorkflowsOrder(String[] workflowIdentifiers, ArrayList<Workflow> workflows) {
        List<String> identifiers = Arrays.asList(workflowIdentifiers);
        Set<Workflow> selection = new HashSet<>();
        for (Workflow workflow : workflows) {
            String identifier = String.valueOf(workflow.getAssay().getIdentifier());
            if (identifiers.contains(identifier)) {
                // TODO see if we can preserve the submission procedure not the identifier order as selected as these are "random"
                selection.add(workflow);
            }
        }
        return selection;
    }

    private void makeGrid(Grid<Workflow> grid, ListDataProvider<Workflow> dataProvider) {
        // Multi selection mode unfortunately SHIFT + is not supported...
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        // Remove all columns and set the specific columns with sorting options
        grid.removeAllColumns();
        grid.setMultiSort(true);
        // Add the columns and make it sortable for convenience if needed
        Grid.Column<Workflow> investigationColumn = grid.addColumn(Workflow::getInvestigationTitle).setHeader("Investigation Title");
        investigationColumn.setSortable(true);
        Grid.Column<Workflow> studyColumn = grid.addColumn(Workflow::getStudyTitle).setHeader("Study Title");
        studyColumn.setSortable(true);
        Grid.Column<Workflow> assayColumn = grid.addColumn(Workflow::getAssayTitle).setHeader("Assay Title");
        assayColumn.setSortable(true);

        // Adding the filter row place holder
        HeaderRow filterRow = grid.appendHeaderRow();

        // Investigation filter
        TextField investigationField = new TextField();
        investigationField.addValueChangeListener(event -> dataProvider.addFilter(
                workflow -> StringUtils.containsIgnoreCase(workflow.getInvestigationTitle(),
                        investigationField.getValue())));

        investigationField.setValueChangeMode(ValueChangeMode.EAGER);

        filterRow.getCell(investigationColumn).setComponent(investigationField);
        investigationField.setSizeFull();
        investigationField.setPlaceholder("Filter");

        // Study filter
        TextField studyField = new TextField();
        studyField.addValueChangeListener(event -> dataProvider.addFilter(
                workflow -> StringUtils.containsIgnoreCase(workflow.getStudyTitle(),
                        studyField.getValue())));

        studyField.setValueChangeMode(ValueChangeMode.EAGER);

        filterRow.getCell(studyColumn).setComponent(studyField);
        studyField.setSizeFull();
        studyField.setPlaceholder("Filter");

        // Assay filter
        TextField assayField = new TextField();
        assayField.addValueChangeListener(event -> dataProvider.addFilter(
                workflow -> StringUtils.containsIgnoreCase(workflow.getAssayTitle(),
                        assayField.getValue())));

        assayField.setValueChangeMode(ValueChangeMode.EAGER);

        filterRow.getCell(assayColumn).setComponent(assayField);
        assayField.setSizeFull();
        assayField.setPlaceholder("Filter");
    }

    private ArrayList<Workflow> populateWorkflow(ArrayList<Investigation> investigations) {
        ArrayList<Workflow> workflows = new ArrayList<>();
        for (Investigation investigation : investigations) {
            for (Study study : investigation.getStudy()) {
                for (Assay assay : study.getAssay()) {
                    Workflow workflow = new Workflow(investigation, study, assay);
                    workflows.add(workflow);
                }
            }
        }
        return workflows;
    }
}
